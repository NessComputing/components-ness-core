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

import java.util.Arrays;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

/**
 * Callback helper methods
 */
public class Callbacks {
    private Callbacks() { }

    /**
     * For every element, invoke the given callback.
     * Stops if {@link CallbackRefusedException} is thrown.
     */
    @SafeVarargs
    public static <T> void stream(Callback<T> callback, T... items) throws Exception
    {
        stream(callback, Arrays.asList(items));
    }

    /**
     * For every element in the iterable, invoke the given callback.
     * Stops if {@link CallbackRefusedException} is thrown.
     */
    @SuppressFBWarnings("BAD_PRACTICE")
    public static <T> void stream(Callback<T> callback, Iterable<T> iterable) throws Exception
    {
        for (T item : iterable) {
            try {
                callback.call(item);
            } catch (CallbackRefusedException e) {
                return;
            }
        }
    }

    /**
     * A callback that does nothing.
     */
    @SuppressWarnings("unchecked")
    public static <T> Callback<T> noop()
    {
        return (Callback<T>) NOOP;
    }

    /**
     * A callback that does nothing.
     */
    public static final Callback<Object> NOOP = new NoopCallback();

    private static class NoopCallback implements Callback<Object>
    {
        @Override
        public void call(Object item) throws Exception { }
    }

    /**
     * Combine multiple callbacks into a single callback, preserving order.
     */
    @SafeVarargs
    public static <T> Callback<T> chain(Callback<T>... callbacks)
    {
        return chain(Arrays.asList(callbacks));
    }

    /**
     * Combine multiple callbacks into a single callback, preserving order.
     */
    public static <T> Callback<T> chain(Iterable<Callback<T>> callbacks)
    {
        return new ChainCallback<T>(callbacks);
    }

    private static class ChainCallback<T> implements Callback<T>
    {
        private final Iterable<Callback<T>> callbacks;

        public ChainCallback(Iterable<Callback<T>> callbacks)
        {
            this.callbacks = callbacks;
        }

        @Override
        public void call(T item) throws Exception
        {
            for (Callback<T> callback : callbacks) {
                callback.call(item);
            }
        }
    }
}
