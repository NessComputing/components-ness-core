package com.nesscomputing.callback;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

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

    private BatchingCallback(int size, Callback<? super List<T>> out)
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
