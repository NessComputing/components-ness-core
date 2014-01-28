/**
 * Copyright (C) 2013 Ness Computing, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nesscomputing.callback;

import static com.nesscomputing.callback.TransformedCallback.transform;

import static org.junit.Assert.assertEquals;

import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import org.junit.Test;

public class TestTransformingCallback
{
    @Test
    public void testSimple() throws Exception
    {
        CallbackCollector<String> callback = new CallbackCollector<>();
        Callback<Integer> tested = transform(callback, new Function<Integer, String>() {
            @Override
            @Nullable
            public String apply(@Nullable Integer input)
            {
                return input == null ? null : Integer.toHexString(input);
            }
        });

        tested.call(null);
        tested.call(15);
        tested.call(0xDEADBEEF);

        assertEquals(Lists.newArrayList(null, "f", "deadbeef"), callback.getItems());
    }

    @Test
    public void testWildcards() throws Exception
    {
        CallbackCollector<Object> callback = new CallbackCollector<>();
        Callback<String> strCallback = transform(callback, Functions.toStringFunction());

        strCallback.call("foo");
        strCallback.call("bar");

        Function<Super, Class<?>> superFunction = new Function<Super, Class<?>>() {
            @Override
            @Nullable
            public Class<?> apply(@Nullable Super input)
            {
                return input.getClass();
            }
        };

        Callback<Super> superCallback = transform(callback, superFunction);
        Callback<Sub> subCallback = transform(callback, superFunction);

        superCallback.call(new Super());
        superCallback.call(new Sub());
        subCallback.call(new Sub());

        assertEquals(Lists.newArrayList("foo", "bar", Super.class, Sub.class, Sub.class), callback.getItems());
    }

    static class Super { }
    static class Sub extends Super { }
}
