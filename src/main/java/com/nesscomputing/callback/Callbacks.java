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

/**
 * Callback helper methods
 */
public class Callbacks {
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
}
