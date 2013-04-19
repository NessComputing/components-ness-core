/**
 * Copyright (C) 2013 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.callback;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;


class ExecutorBatchingCallback<T> extends BatchingCallback<T>
{
    ExecutorBatchingCallback(int size, ExecutorService executor, Callback<? super List<T>> out, boolean failFast)
    {
        super(size, new ExecutorCallback<>(executor, out, failFast));
    }

    @Override
    public boolean commit()
    {
        boolean result = super.commit();
        ExecutorCallback.class.cast(getOut()).close();
        return result;
    }

    static class ExecutorCallback<T> implements Callback<List<T>>
    {
        private final ExecutorCompletionService<Void> executor;
        private final Callback<? super List<T>> out;
        private final AtomicLong inFlight = new AtomicLong();
        private final BatchingCallbackExecutionException exceptions = new BatchingCallbackExecutionException();
        private final AtomicBoolean failed = new AtomicBoolean();
        private final boolean failFast;

        ExecutorCallback(ExecutorService executor, Callback<? super List<T>> out, boolean failFast)
        {
            this.executor = new ExecutorCompletionService<Void>(executor);
            this.out = out;
            this.failFast = failFast;
        }

        @Override
        public void call(final List<T> item) throws Exception
        {
            if (failed.get()) {
                throw new CallbackRefusedException();
            }

            inFlight.incrementAndGet();
            executor.submit(new ExecutorCallable<T>(out, item));

            Future<Void> f;
            while ( (f = executor.poll()) != null ) {
                inFlight.decrementAndGet();
                try {
                    f.get();
                } catch (ExecutionException e) {
                    exceptions.addSuppressed(e.getCause());

                    if (failFast) {
                        failed.set(true);
                        exceptions.fillInStackTrace();
                        throw exceptions;
                    }
                }
            }
        }

        public void close()
        {
            while (inFlight.decrementAndGet() > 0) {
                try {
                    executor.take().get();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    throw new IllegalStateException(e);
                } catch (ExecutionException e) {
                    exceptions.addSuppressed(e.getCause());
                }
            }

            if (exceptions.getSuppressed().length != 0) {
                exceptions.fillInStackTrace();
                throw exceptions;
            }
        }
    }

    static class ExecutorCallable<T> implements Callable<Void>
    {
        private final Callback<? super List<T>> out;
        private final List<T> item;

        ExecutorCallable(Callback<? super List<T>> out, List<T> item)
        {
            this.out = out;
            this.item = item;
        }

        @Override
        public Void call() throws Exception
        {
            out.call(item);
            return null;
        }
    }
}
