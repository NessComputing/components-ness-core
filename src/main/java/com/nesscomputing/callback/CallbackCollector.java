package com.nesscomputing.callback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * A {@link Callback} which collects all items into a list for later retrieval.
 * Not appropriate for large sets as you may exhaust heap space.
 * Preserves ordering.
 */
@NotThreadSafe
public class CallbackCollector<T> implements Callback<T> {

    private final List<T> collected = Collections.synchronizedList(new ArrayList<T>());

    @Override
    public void call(T item) throws Exception {
        collected.add(item);
    }

    public List<T> getItems() {
        return collected;
    }
}
