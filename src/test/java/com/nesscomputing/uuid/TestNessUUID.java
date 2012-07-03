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
    private final String overflow = "FFFFFFFF-FFFF-FFFF-FFFF-FFFFFFFFFFFF";
    private final String zero = "00000000-0000-0000-0000-000000000000";
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
        Assert.assertEquals(UUID.fromString(overflow), NessUUID.fromString(overflow));
        Assert.assertEquals(UUID.fromString(zero), NessUUID.fromString(zero));
    }

    @Test
    public void testLength() {
        final UUID uuid = NessUUID.fromString(badLength);
        Assert.assertEquals(0, uuid.getMostSignificantBits());
        Assert.assertEquals(0, uuid.getLeastSignificantBits());
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

    /*
     * Add the luni tests from http://svn.apache.org/viewvc/harmony/enhanced/java/branches/java6/classlib/modules/luni/src/test/api/common/org/apache/harmony/luni/tests/java/util/UUIDTest.java?revision=929252
     */

    /**
     * @see UUID#fromString(String)
     */
    @Test
    public void test_fromString() {
        UUID actual = NessUUID.fromString("f81d4fae-7dec-11d0-a765-00a0c91e6bf6");
        UUID expected = new UUID(0xf81d4fae7dec11d0L, 0xa76500a0c91e6bf6L);
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(2, actual.variant());
        Assert.assertEquals(1, actual.version());
        Assert.assertEquals(130742845922168750L, actual.timestamp());
        Assert.assertEquals(10085, actual.clockSequence());
        Assert.assertEquals(690568981494L, actual.node());

        actual = NessUUID.fromString("00000000-0000-1000-8000-000000000000");
        expected = new UUID(0x0000000000001000L, 0x8000000000000000L);
        Assert.assertEquals(expected, actual);

        Assert.assertEquals(2, actual.variant());
        Assert.assertEquals(1, actual.version());
        Assert.assertEquals(0L, actual.timestamp());
        Assert.assertEquals(0, actual.clockSequence());
        Assert.assertEquals(0L, actual.node());

        try {
            NessUUID.fromString(null);
            Assert.fail("No NPE");
        } catch (NullPointerException e) {}

        try {
            NessUUID.fromString("");
            Assert.fail("No IAE");
        } catch (IllegalArgumentException e) {}

        try {
            NessUUID.fromString("f81d4fae_7dec-11d0-a765-00a0c91e6bf6");
            Assert.fail("No IAE");
        } catch (IllegalArgumentException e) {}

        try {
            NessUUID.fromString("f81d4fae-7dec_11d0-a765-00a0c91e6bf6");
            Assert.fail("No IAE");
        } catch (IllegalArgumentException e) {}

        try {
            NessUUID.fromString("f81d4fae-7dec-11d0_a765-00a0c91e6bf6");
            Assert.fail("No IAE");
        } catch (IllegalArgumentException e) {}

        try {
            NessUUID.fromString("f81d4fae-7dec-11d0-a765_00a0c91e6bf6");
            Assert.fail("No IAE");
        } catch (IllegalArgumentException e) {}
    }

	/**
	 * @tests java.util.UUID#fromString(String)
	 */
    @Test
	public void test_fromString_LString_Exception() {

		UUID uuid = NessUUID.fromString("0-0-0-0-0");

		try {
			uuid = NessUUID.fromString("0-0-0-0-");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("-0-0-0-0-0");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("-0-0-0-0");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("-0-0-0-");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("0--0-0-0");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("0-0-0-0-");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("-1-0-0-0-0");
			Assert.fail("should throw IllegalArgumentException");
		} catch (IllegalArgumentException e) {
			// expected
		}

		uuid = UUID.fromString("123456789-0-0-0-0");
		Assert.assertEquals(0x2345678900000000L, uuid.getMostSignificantBits());
		Assert.assertEquals(0x0L, uuid.getLeastSignificantBits());

		uuid = NessUUID.fromString("111123456789-0-0-0-0");
		Assert.assertEquals(0x2345678900000000L, uuid.getMostSignificantBits());
		Assert.assertEquals(0x0L, uuid.getLeastSignificantBits());

		uuid = NessUUID.fromString("7fffffffffffffff-0-0-0-0");
		Assert.assertEquals(0xffffffff00000000L, uuid.getMostSignificantBits());
		Assert.assertEquals(0x0L, uuid.getLeastSignificantBits());

		try {
			uuid = NessUUID.fromString("8000000000000000-0-0-0-0");
			Assert.fail("should throw NumberFormatException");
		} catch (NumberFormatException e) {
			// expected
		}

		uuid = UUID
				.fromString("7fffffffffffffff-7fffffffffffffff-7fffffffffffffff-0-0");
		Assert.assertEquals(0xffffffffffffffffL, uuid.getMostSignificantBits());
		Assert.assertEquals(0x0L, uuid.getLeastSignificantBits());

		uuid = NessUUID.fromString("0-0-0-7fffffffffffffff-7fffffffffffffff");
		Assert.assertEquals(0x0L, uuid.getMostSignificantBits());
		Assert.assertEquals(0xffffffffffffffffL, uuid.getLeastSignificantBits());

		try {
			uuid = NessUUID.fromString("0-0-0-8000000000000000-0");
			Assert.fail("should throw NumberFormatException");
		} catch (NumberFormatException e) {
			// expected
		}

		try {
			uuid = NessUUID.fromString("0-0-0-0-8000000000000000");
			Assert.fail("should throw NumberFormatException");
		} catch (NumberFormatException e) {
			// expected
		}
	}
}
