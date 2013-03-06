package com.nesscomputing.callback;

import static com.google.common.collect.ImmutableList.of;
import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

import org.junit.Test;

public class TestBatchingCallback
{
    @Test
    public void testBatchingCallback() throws Exception
    {
        CallbackCollector<List<String>> collector = new CallbackCollector<>();
        try (BatchingCallback<String> batcher = BatchingCallback.batchInto(2, collector)) {
            batcher.call("a");
            batcher.call("b");
            batcher.call("c");
            batcher.commit();
            batcher.call("d");
            batcher.call("e");
            batcher.call("f");
        }

        assertEquals(of(
                of("a", "b"),
                of("c"),
                of("d", "e"),
                of("f")
            ), collector.getItems());
    }

    @Test
    public void testCallbackRefused() throws Exception
    {
        final List<Collection<String>> items = Lists.newArrayList();
        Callback<Collection<String>> callback = new Callback<Collection<String>>() {
            @Override
            public void call(Collection<String> item) throws Exception
            {
                if (items.size() > 1) {
                    throw new CallbackRefusedException();
                }
                items.add(item);
            }
        };

        try (BatchingCallback<String> batcher = BatchingCallback.<String>batchInto(2, callback)) {
            Callbacks.stream(batcher, "a", "b", "c", "d", "e", "f", "g");
        }

        assertEquals(of(
                of("a", "b"),
                of("c", "d")
            ), items);
    }
}
