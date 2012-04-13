package com.nesscomputing.callback;

import java.util.concurrent.atomic.AtomicLong;

import javax.annotation.concurrent.ThreadSafe;

/**
 * A Callback that throws away all objects but counts them as it does so
 */
@ThreadSafe
public class CountingCollector implements Callback<Object> {

    private final AtomicLong count = new AtomicLong();

    public long getCount() {
        return count.get();
    }

    @Override
    public void call(Object item) throws Exception {
        count.incrementAndGet();
    }
}
