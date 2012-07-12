package com.nesscomputing.callback;

import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import junit.framework.Assert;

import org.junit.Test;

import com.google.common.collect.Iterators;

public class PipeCallbackTest
{
    @Test(timeout=10000)
    public void testNoElements() throws Exception
    {
        final AtomicBoolean success = new AtomicBoolean(true);

        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> callback) throws Exception
            {
                // do nothing
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                if (item.hasNext())
                {
                    success.set(false);
                }
            }
        });

        Assert.assertTrue(success.get());
    }

    @Test(timeout=10000)
    public void testSingleElement() throws Exception
    {
        final AtomicBoolean success = new AtomicBoolean();

        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> item) throws Exception
            {
                item.call("foo");
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                if ("foo".equals(Iterators.getOnlyElement(item)))
                {
                    success.set(true);
                }
            }
        });

        Assert.assertTrue(success.get());
    }

    @Test(timeout=10000, expected=InterruptedException.class)
    public void testMainThreadInterrupt() throws Exception
    {
        final Thread mainThread = Thread.currentThread();

        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> item) throws Exception
            {
                item.call("foo");
                if (mainThread.isInterrupted())
                {
                    throw new InterruptedException();
                }
                Assert.fail();
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                mainThread.interrupt();
                if (item.hasNext() && !"foo".equals(item.next()))
                {
                    Assert.fail();
                }
            }
        });

        Assert.fail();
    }

    @Test(timeout=10000, expected=IllegalStateException.class)
    public void testWorkerThreadInterrupt() throws Exception
    {
        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> item) throws Exception
            {
                item.call("foo");
                item.call("bar");
                item.call("baz");
                Assert.fail();
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                throw new InterruptedException();
            }
        });

        Assert.fail();
    }

    @Test(timeout=10000, expected=IllegalStateException.class)
    public void testMainThreadException() throws Exception
    {
        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> item) throws Exception
            {
                item.call("foo");
                throw new IllegalStateException();
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                if (!"foo".equals(item.next()))
                {
                    Assert.fail();
                }
                Assert.assertFalse(item.hasNext());
            }
        });

        Assert.fail();
    }

    @Test(timeout=10000, expected=IllegalStateException.class)
    public void testWorkerThreadException() throws Exception
    {
        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> item) throws Exception
            {
                item.call("foo");
                item.call("bar");
                item.call("baz");
                Assert.fail();
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> item) throws Exception
            {
                throw new IllegalStateException();
            }
        });

        Assert.fail();
    }

    @Test(timeout=10000)
    public void testManyElements() throws Exception
    {
        Callbacks.pipeToIterator(new Callback<Callback<String>>() {
            @Override
            public void call(Callback<String> callback) throws Exception
            {
                for (int i = 0; i < 10000; i++)
                {
                    callback.call(Integer.toString(i));
                }
            }
        }, new Callback<Iterator<String>>() {
            @Override
            public void call(Iterator<String> iter) throws Exception
            {
                for (int i = 0; i < 1000; i++)
                {
                    Assert.assertEquals(i, Integer.parseInt(iter.next()));
                }
            }
        });
    }
}
