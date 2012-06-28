/**
 * Copyright (C) 2012 Ness Computing, Inc.
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
package com.nesscomputing.uuid;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class TestNessUUID {
    private final String uuid = "6f32f693-c7b5-11e1-afa7-88af2abc9a66";
    private final String caseSensitivity = "Dd000000-0000-0000-0000-000000000000";
    private final String badLength = "00000000-f0000-0000-0000-000000000000";
    private final String hyphen1 = "00000000f0000-0000-0000-000000000000";
    private final String hyphen2 = "00000000-0000f0000-0000-000000000000";
    private final String hyphen3 = "00000000-0000-0000f0000-000000000000";
    private final String hyphen4 = "00000000-0000-0000-0000f000000000000";
    private final String invalid1 = "00000000-0000-0-00-0000-000000000000";
    private final String invalid2 = "0000g000-0000-0000-0000-000000000000";
    private final String invalid3 = "00000000-00g0-0000-0000-000000000000";
    private final String invalid4 = "00000000-0000-0g00-0000-000000000000";
    private final String invalid5 = "00000000-0000-0000-00g0-000000000000";
    private final String invalid6 = "00000000-0000-0000-0000-0000000000g0";

    @Test
    public void testBasic()
    {
        Assert.assertEquals(UUID.fromString(uuid), NessUUID.fromString(uuid));
        Assert.assertEquals(UUID.fromString(caseSensitivity), NessUUID.fromString(caseSensitivity));
    }

    @Test(expected=IllegalArgumentException.class)
    public void testLength() {
        NessUUID.fromString(badLength);
    }

    @Test
    public void testHyphen() {
        testEx(hyphen1);
        testEx(hyphen2);
        testEx(hyphen3);
        testEx(hyphen4);
    }

    @Test
    public void invalid() {
        testEx(invalid1);
        testEx(invalid2);
        testEx(invalid3);
        testEx(invalid4);
        testEx(invalid5);
        testEx(invalid6);
    }

    // makes testing multiple exceptions less verbose
    private void testEx(String str) {
        try {
            NessUUID.fromString(hyphen1);
        } catch (IllegalArgumentException e) {
            return;
        }
        Assert.fail(str);
    }
}