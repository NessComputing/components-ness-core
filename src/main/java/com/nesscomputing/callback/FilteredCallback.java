package com.nesscomputing.callback;

import com.google.common.base.Predicate;

/**
 * Simple callback that wraps another and only passes through items matching the given predicate.
 */
public class FilteredCallback<T> implements Callback<T> {

    private final Callback<T> delegate;
    private final Predicate<T> filter;

    public FilteredCallback(Callback<T> delegate, Predicate<T> filter) {
        this.delegate = delegate;
        this.filter = filter;
    }

    @Override
    public void call(T item) throws Exception {
        if (filter.apply(item)) {
            delegate.call(item);
        }
    }
}
