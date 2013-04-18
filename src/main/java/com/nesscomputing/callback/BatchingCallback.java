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

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;

/**
 * Collect incoming items into batches of a fixed size, and invoke
 * a delegate callback whenever a complete batch is available.
 * This callback buffers items, so it must be committed when finished.
 * The easiest way to accomplish this is with a {@code try-with-resources} statement.
 * <pre>
 * Callback&lt;List&lt;String&gt;&gt; writeStringToDisk = ...;
 * try (BatchingCallback&lt;String&gt; callback = BatchingCallback.batchInto(100, writeStringToDisk)) {
 *     doQuery(Queries.allItems(), callback);
 * }
 * </pre>
 */
public class BatchingCallback<T> implements Callback<T>, Closeable
{
    private final BlockingQueue<T> list;
    private final Callback<? super List<T>> out;
    private final int size;

    BatchingCallback(int size, Callback<? super List<T>> out)
    {
        if (size <= 0) {
            throw new IllegalArgumentException("Size must be positive, was " + size);
        }
        if (out == null) {
            throw new IllegalArgumentException("Null callback");
        }
        this.size = size;
        list = new ArrayBlockingQueue<>(size);
        this.out = out;
    }

    /**
     * Collect {@code <T>} into a buffer, and invoke the given callback whenever
     * the buffer is full, during an explicit commit, or on close.
     */
    public static <T> BatchingCallback<T> batchInto(int size, Callback<? super List<T>> out)
    {
        return new BatchingCallback<T>(size, out);
    }

    /**
     * Collect {@code <T>} into a buffer, and schedule the given callback with the given executor
     * whenever the buffer is full.  If failFast is false and any exceptions are thrown, a
     * {@link BatchingCallbackExecutionException} is thrown when the BatchingCallback is {@link #commit()}ed.
     * It suppresses all of the other thrown exceptions.  If failFast is true and an exception is thrown, it
     * is rethrown as soon as it is noticed and further invocations will generate {@link CallbackRefusedException}.
     */
    public static <T> BatchingCallback<T> batchInto(int size, ExecutorService executor, Callback<? super List<T>> out, boolean failFast)
    {
        return new ExecutorBatchingCallback<T>(size, executor, out, failFast);
    }

    /**
     * Add an item to the buffer.  May cause a commit if the buffer is full.
     * @throws CallbackRefusedException if the delegate throws.
     */
    @Override
    public void call(T item) throws CallbackRefusedException
    {
        while (!list.offer(item)) {
            commitInternal();
        }
    }

    /**
     * Alternate method of committing, for use with {@code try-with-resources}.
     */
    @Override
    public void close()
    {
        commit();
    }

    /**
     * Explicitly flush the buffer, even if it is not full.
     * @return true if the flush succeeds, false if the delegate throws {@code CallbackRefusedException}
     */
    public boolean commit()
    {
        try {
            commitInternal();
            return true;
        } catch (CallbackRefusedException e) {
            return false;
        }
    }

    Callback<? super List<T>> getOut()
    {
        return out;
    }

    private void commitInternal() throws CallbackRefusedException
    {
        final List<T> outList = new ArrayList<T>(size);
        list.drainTo(outList);
        if (!outList.isEmpty()) {
            try {
                out.call(outList);
            } catch (final CallbackRefusedException | RuntimeException e) {
                throw e;
            } catch (final Exception e) {
                throw new RuntimeException(e);
            }
        }
    }
}
