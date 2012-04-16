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
}
