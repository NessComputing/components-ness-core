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


/**
 * A class that provides an alternate implementation of {@link
 * java.util.UUID#fromString(String)}.
 *
 * <p> The version in the JDK uses {@link java.lang.String#split(String)}
 * which does not compile the regular expression that is used for splitting
 * the UUID string and results in the allocation of multiple strings in a
 * string array. We decided to write {@link NessUUID} when we ran into
 * performance issues with the garbage produced by {@link
 * java.util.UUID#fromString(String)}.
 *
 */
public class NessUUID {
    private NessUUID() {}

    private static final byte[] CHAR_MAP = new byte[Character.MAX_VALUE];

    static {
        for (int i = 0; i < Character.MAX_VALUE; i++) {
            CHAR_MAP[i] = -1;
        }
        initCharMap('a', 'f', 10);
        initCharMap('A', 'F', 10);
        initCharMap('0', '9', 0);
    }

    public static UUID fromString(String str) {
        int dashCount = 4;
        int [] dashPos = new int [6];
        dashPos[0] = -1;
        dashPos[5] = str.length();

        for (int i = str.length()-1; i >= 0; i--) {
            if (str.charAt(i) == '-') {
                if (dashCount == 0) {
                    throw new IllegalArgumentException("Invalid UUID string: " + str);
                }
                dashPos[dashCount--] = i;
            }
        }

        if (dashCount > 0) {
            throw new IllegalArgumentException("Invalid UUID string: " + str);
        }

        long mostSigBits = decode(str, dashPos, 0) & 0xffffffffL;
        mostSigBits <<= 16;
        mostSigBits |= (decode(str, dashPos, 1) & 0xffffL);
        mostSigBits <<= 16;
        mostSigBits |= (decode(str,  dashPos, 2) & 0xffffL);

        long leastSigBits = (decode(str,  dashPos, 3) & 0xffffL);
        leastSigBits <<= 48;
        leastSigBits |= (decode(str,  dashPos, 4) & 0xffffffffffffL);

        return new UUID(mostSigBits, leastSigBits);
    }

    private static long decode(final String str, final int [] dashPos, final int field) {
        int start = dashPos[field]+1;
        int end = dashPos[field+1];
        if (start >= end) {
            throw new IllegalArgumentException("Invalid UUID string: " + str);
        }
        long curr = 0;
        for (int i = start; i < end; i++) {
            byte v = CHAR_MAP[str.charAt(i)];
            if (v == -1) {
                throw new IllegalArgumentException("Invalid UUID string: " + str);
            }
            curr <<= 4;
            if (curr < 0) {
                throw new NumberFormatException("long overflow");
            }
            curr |= v;
        }
        return curr;
    }

    private static void initCharMap(final char a, final char b, final int offset) {
        for (int i = a; i <= b; i++) {
            CHAR_MAP[i] = (byte) ((i - a) + offset);
        }
    }
}
