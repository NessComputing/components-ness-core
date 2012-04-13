package com.nesscomputing.callback;

public interface Callback<T> {
    void call(T item) throws Exception;
}
