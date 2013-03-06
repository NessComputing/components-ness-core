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
