package com.nesscomputing.callback;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.concurrent.ExecutorService;

import com.google.common.util.concurrent.MoreExecutors;

import org.junit.Test;

public class TestExecutorBatchingCallback
{
    @Test
    public void testGood() throws Exception
    {
        ExecutorService executor = MoreExecutors.sameThreadExecutor();
        CallbackCollector<List<String>> out = new CallbackCollector<>();
        BatchingCallback<String> batcher = BatchingCallback.batchInto(2, executor, out, false);

        batcher.call("a");
        batcher.call("b");
        batcher.call("c");
        batcher.call("d");
        batcher.call("e");

        batcher.commit();

        assertEquals(of(of("a", "b"), of("c", "d"), of("e")), out.getItems());
    }

    @Test
    public void testFailSlow() throws Exception
    {
        final Exception e1 = new Exception();
        final Exception e2 = new Exception();

        ExecutorService executor = MoreExecutors.sameThreadExecutor();
        Callback<List<String>> out = new Callback<List<String>>() {
            @Override
            public void call(List<String> item) throws Exception
            {
                switch (item.get(0)) {
                case "a":
                    throw e1;
                case "c":
                    return;
                case "e":
                    throw e2;
                }
            }
        };

        BatchingCallback<String> batcher = BatchingCallback.batchInto(2, executor, out, false);

        batcher.call("a");
        batcher.call("b");
        batcher.call("c");
        batcher.call("d");
        batcher.call("e");

        try {
            batcher.commit();
            fail();
        } catch (BatchingCallbackExecutionException bcee) {
            assertEquals(2, bcee.getSuppressed().length);
            assertSame(e1, bcee.getSuppressed()[0]);
            assertSame(e2, bcee.getSuppressed()[1]);
        }
    }

    @Test
    public void testFailFast() throws Exception
    {
        final Exception e1 = new Exception();
        final Exception e2 = new Exception();

        ExecutorService executor = MoreExecutors.sameThreadExecutor();
        Callback<List<String>> out = new Callback<List<String>>() {
            @Override
            public void call(List<String> item) throws Exception
            {
                switch (item.get(0)) {
                case "a":
                    throw e1;
                case "c":
                    return;
                case "e":
                    throw e2;
                }
            }
        };

        BatchingCallback<String> batcher = BatchingCallback.batchInto(2, executor, out, true);

        batcher.call("a");
        batcher.call("b");
        try {
            batcher.call("c");
            fail();
        } catch (BatchingCallbackExecutionException bcee) {
            assertEquals(1, bcee.getSuppressed().length);
            assertSame(e1, bcee.getSuppressed()[0]);
        }
        try {
            batcher.call("d");
            batcher.call("e");
            batcher.call("f");
            fail();
        } catch (CallbackRefusedException e) {
        }
    }
}
