package com.nesscomputing.callback;


public class Callbacks {
    private Callbacks() { }

    public static <T> void stream(Callback<T> callback, Iterable<T> iterable) throws Exception {
        for (T item : iterable) {
            callback.call(item);
        }
    }
}
