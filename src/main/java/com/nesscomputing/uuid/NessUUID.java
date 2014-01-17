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
package com.nesscomputing.uuid;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.util.UUID;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;

/**
 * A class that provides an alternate implementation of {@link
 * UUID#fromString(String)} and {@link UUID#toString()}.
 *
 * <p> The version in the JDK uses {@link String#split(String)}
 * which does not compile the regular expression that is used for splitting
 * the UUID string and results in the allocation of multiple strings in a
 * string array. We decided to write {@link NessUUID} when we ran into
 * performance issues with the garbage produced by the JDK class.
 *
 */
public final class NessUUID {
    private NessUUID() {}

    private static final MethodHandle STRING_FACTORY;

    private static final int NUM_ALPHA_DIFF = 'A' - '9' - 1;
    private static final int LOWER_UPPER_DIFF = 'a' - 'A';

    // FROM STRING

    public static UUID fromString(String str) {
        try {
            int dashCount = 4;
            final int [] dashPos = new int [6];
            dashPos[0] = -1;
            dashPos[5] = str.length();

            for (int i = str.length()-1; i >= 0; i--) {
                if (str.charAt(i) == '-') {
                    if (dashCount == 0) {
                        throw new IllegalArgumentException("Too many dashes (-)");
                    }
                    dashPos[dashCount--] = i;
                }
            }

            if (dashCount > 0) {
                throw new IllegalArgumentException("Not enough dashes (-)");
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
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid UUID string: " + str, e);
        }
    }

    @VisibleForTesting
    static long decode(final String str, final int [] dashPos, final int field) {
        final int start = dashPos[field]+1;
        final int end = dashPos[field+1];
        if (start >= end) {
            throw new IllegalArgumentException(String.format("In call to decode(), start (%d) >= end (%d)", start, end));
        }
        // at most 16 nibbles are allowed (64 bits)
        else if (end - start > 16) {
            throw new NumberFormatException("long overflow");
        }

        long curr = 0;
        for (int i = start; i < end; i++) {
            curr <<= 4;
            curr |= getNibbleFromChar(str.charAt(i));
        }
        return curr;
    }

    static int getNibbleFromChar(final char c)
    {
        int x = c - '0';
        if (x > 9) {
            x -= NUM_ALPHA_DIFF; // difference between '9' and 'A'
            if (x > 15) {
                x -= LOWER_UPPER_DIFF; // difference between 'a' and 'A'
            }
            if (x < 10) {
                throw new IllegalArgumentException(c + " is not a valid character for an UUID string");
            }
        }

        if (x < 0 || x > 15) {
            throw new IllegalArgumentException(c + " is not a valid character for an UUID string");
        }

        return x;
    }

    // TO STRING

    public static String toString(UUID uuid)
    {
        return toString(uuid.getMostSignificantBits(), uuid.getLeastSignificantBits());
    }

    /** Roughly patterned (read: stolen) from java.util.UUID and java.lang.Long. */
    public static String toString(long msb, long lsb)
    {
        final char[] uuidChars = new char[36];

        digits(uuidChars, 0, 8, msb >> 32);
        uuidChars[8] = '-';
        digits(uuidChars, 9, 4, msb >> 16);
        uuidChars[13] = '-';
        digits(uuidChars, 14, 4, msb);
        uuidChars[18] = '-';
        digits(uuidChars, 19, 4, lsb >> 48);
        uuidChars[23] = '-';
        digits(uuidChars, 24, 12, lsb);

        try {
            return (String) STRING_FACTORY.invokeExact(uuidChars);
        } catch (Throwable e) {
            throw Throwables.propagate(e);
        }
    }

    private static void digits(char[] dest, int offset, int digits, long val) {
        final long hi = 1L << (digits * 4);
        toUnsignedString(dest, offset, digits, hi | (val & (hi - 1)), 4);
    }

    private final static char[] DIGITS = {
        '0' , '1' , '2' , '3' , '4' , '5' ,
        '6' , '7' , '8' , '9' , 'a' , 'b' ,
        'c' , 'd' , 'e' , 'f'
    };

    private static void toUnsignedString(final char[] dest, final int offset, final int len, final long value, final int shift) {
        int charPos = len;
        final int radix = 1 << shift;
        final long mask = radix - 1;
        long i = value;
        do {
            dest[offset + --charPos] = DIGITS[(int)(i & mask)];
            i >>>= shift;
        } while (i != 0 && charPos > 0);
    }

    static {
        Constructor<String> constructor = null;

        try {
            constructor = String.class.getDeclaredConstructor(char[].class, boolean.class);
            constructor.setAccessible(true);
        } catch (NoSuchMethodException | SecurityException e) { // NOPMD
            e.printStackTrace(); // NOPMD logging may not be available in static constructor of ness-core
        }

        MethodHandle factory;
        try {
            factory = MethodHandles.lookup().findConstructor(String.class, MethodType.methodType(void.class, char[].class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            throw new ExceptionInInitializerError(e);
        }
        if (constructor != null) {
            try {
                final MethodHandle fastFactory = MethodHandles.insertArguments(
                        MethodHandles.lookup().unreflectConstructor(constructor),
                        1, true);

                final char[] unsharedChars = "test".toCharArray();
                String testString = (String) fastFactory.invokeExact(unsharedChars);
                Preconditions.checkState("test".equals(testString));

                unsharedChars[0] = 'r';
                Preconditions.checkState("rest".equals(testString)); // :O

                factory = fastFactory;
            } catch (Throwable e) { // NOPMD
                e.printStackTrace(); // NOPMD logging may not be available in static constructor of ness-core
            }
        }
        STRING_FACTORY = factory;
    }
}
