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
package com.nesscomputing.util;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class TestSizes
{
    private static final int KB = 1024;
    private static final int MB = KB * 1024;

    @Test
    public void testSizes() throws Exception
    {
        assertEquals("1.0 MiB", Sizes.formatSize(MB));
        assertEquals("1.5 MiB", Sizes.formatSize((long) (1.5 * MB)));

        assertEquals("9.6 TiB", Sizes.formatSize((long) (9.6 * MB * MB)));
    }

    @Test
    public void testRates() throws Exception
    {
        assertEquals("1.0 MiB/s", Sizes.formatRate(MB * 5, 5, TimeUnit.SECONDS));
        assertEquals("1.2 GiB/s", Sizes.formatRate((long) (1.2 * MB), 1, TimeUnit.MILLISECONDS));
    }
}
