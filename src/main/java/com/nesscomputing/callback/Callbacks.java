/**
 * Copyright (C) 2012 Ness Computing, Inc.
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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.common.annotations.Beta;
import com.google.common.base.Throwables;
import com.google.common.collect.Iterators;
import com.mogwee.executors.Executors;

/**
 * Callback helper methods
 */
public class Callbacks {
    private static final int POISON_PILL_GIVEUP_MS = 10;
    private static final int CHILD_DEATH_POLL_INTERVAL_MS = 1000;

    private Callbacks() { }

    /**
     * For every element in the iterable, invoke the given callback.
     */
    public static <T> void stream(Callback<T> callback, Iterable<T> iterable) throws Exception {
        for (T item : iterable) {
            callback.call(item);
        }
    }

    /**
     * Pipe with a default name.
     * @see #pipeToIterable(Callback, Callback, String)
     */
    @Beta
    public static <T> void pipeToIterator(Callback<Callback<T>> fromSource, Callback<Iterator<T>> intoCallback)
    throws InterruptedException
    {
        pipeToIterator(fromSource, intoCallback, "pipe-" + Thread.currentThread().getName() + "-" + UUID.randomUUID());
    }

    /**
     * Limited adapter from a {@link Callback} to an {@link Iterator}.  Performs roughly the inverse operation of
     * {@link #stream(Callback, Iterable)}.  Requires two callbacks.  The first is passed a Callback
     * which is the 'source' end of the pipe.  The second is passed an Iterator, the 'consuming' end of the pipe.
     * Every element pushed into the source Callback will come out at the consuming Iterator.  Due to the push-pull
     * nature of this operation, the use of an auxiliary thread is required.
     *
     * <p>While in general we encourage writing all API producers and consumers to use Callbacks directly,
     * it is occasionally desirable to interface with an Iterator-based API that is either mandated externally
     * or is inconvenient to change.
     *
     * <p>The somewhat inconvenient method call signature (with everything wrapped again in Callbacks) is
     * required to ensure correct resource management.
     *
     * <p>If the either thread is interrupted, the main thread terminates with an exception
     * and the worker thread dies.
     *
     * @param fromSource invoked with the source Callback
     * @param intoCallback invoked with the destination Iterator
     * @param pipeName the name of the auxiliary thread
     */
    @Beta
    public static <T> void pipeToIterator(
            final Callback<Callback<T>> fromSource,
            final Callback<Iterator<T>> intoCallback,
            final String pipeName)
    throws InterruptedException
    {
        final SynchronousQueue<T> queue = new SynchronousQueue<T>();

        @SuppressWarnings("unchecked") // Used only for == checks
        final T poisonPill = (T) new Object();

        final ExecutorService pipe = Executors.newSingleThreadExecutor(pipeName);

        // Set when the child thread exits
        final AtomicBoolean childThreadAborted = new AtomicBoolean();

        // Thrown as a marker from the callback if the child thread unexpectedly exits
        final Exception childTerminated = new Exception();

        try {
            pipe.submit(new Callable<Void>() {
                @Override
                public Void call() throws Exception
                {
                    try {
                        // Read items off of the queue until we see the poison pill
                        QueueIterator<T> iterator = new QueueIterator<T>(queue, poisonPill);
                        intoCallback.call(iterator);
                        Iterators.getLast(iterator); // Consume the rest of the iterator, in case the callback returns early
                        return null;
                    } finally
                    {
                        childThreadAborted.set(true);
                    }
                }
            });
        } finally { // Ensure we always shutdown the service so that we never leak threads
            pipe.shutdown();
        }

        try {
            // Offer items from the callback into the queue
            fromSource.call(new Callback<T>() {
                @Override
                public void call(T item) throws Exception
                {
                    // Check periodically if the child thread is dead, and give up
                    while (!queue.offer(item, CHILD_DEATH_POLL_INTERVAL_MS, TimeUnit.MILLISECONDS))
                    {
                        if (childThreadAborted.get())
                        {
                            throw childTerminated;
                        }
                    }
                }
            });
        } catch (InterruptedException e) {
            // Give up, let the finally block kill the worker
            Thread.currentThread().interrupt();
            throw e;
        } catch (Exception e) {
            if (e == childTerminated) { // The child thread died unexpectedly
                throw new IllegalStateException("worker thread interrupted");
            }
            throw Throwables.propagate(e);
        } finally {
            try
            {
                if (!childThreadAborted.get())
                {
                    queue.offer(poisonPill, POISON_PILL_GIVEUP_MS, TimeUnit.MILLISECONDS);
                }
            } catch (InterruptedException e)
            {
                Thread.currentThread().interrupt();
                throw Throwables.propagate(e);
            } finally
            {
                pipe.shutdownNow();
            }
        }
    }

    /**
     * Consume elements from a queue until a given poison pill is found.
     */
    private static class QueueIterator<T> implements Iterator<T>
    {
        private SynchronousQueue<T> queue;
        private T element;
        private T poisonPill;

        QueueIterator(SynchronousQueue<T> queue, T poisonPill)
        {
            this.queue = queue;
            this.poisonPill = poisonPill;
        }

        @Override
        public boolean hasNext()
        {
            ensureNext();
            return element != poisonPill;
        }

        @Override
        public T next()
        {
            ensureNext();
            if (element == poisonPill)
            {
                throw new NoSuchElementException();
            }
            try {
                return element;
            }
            finally {
                element = null;
            }
        }

        private void ensureNext()
        {
            if (element == null)
            {
                try
                {
                    element = queue.take();
                } catch (InterruptedException e)
                {
                    Thread.currentThread().interrupt();
                    throw Throwables.propagate(e);
                }
            }
        }

        @Override
        public void remove()
        {
            throw new UnsupportedOperationException();
        }
    }
}
