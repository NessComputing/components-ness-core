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

import com.google.common.base.Function;

public class TransformedCallback<A, B> implements Callback<A>
{
    private final Callback<? super B> callback;
    private final Function<? super A, ? extends B> transformer;

    /**
     * Create a new Callback which transforms its items according to a {@link Function}
     * and then invokes the original callback.
     */
    public static <A, B> Callback<A> transform(Callback<? super B> callback, Function<? super A, ? extends B> transformer)
    {
        return new TransformedCallback<A, B>(callback, transformer);
    }

    TransformedCallback(Callback<? super B> callback, Function<? super A, ? extends B> transformer)
    {
        this.callback = callback;
        this.transformer = transformer;
    }

    @Override
    public void call(A item) throws Exception
    {
        callback.call(transformer.apply(item));
    }
}
