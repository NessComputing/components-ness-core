package com.nesscomputing.uuid;

import java.util.UUID;

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
        if (str.length() != 36 ||
            str.charAt(8) != '-' ||
            str.charAt(13) != '-' ||
            str.charAt(18) != '-' ||
            str.charAt(23) != '-') {
            throw new IllegalArgumentException("Invalid UUID string: " + str);
        }

        long mostSigBits = decode(str, 0, 8);
        mostSigBits <<= 16;
        mostSigBits |= decode(str, 9, 4);
        mostSigBits <<= 16;
        mostSigBits |= decode(str, 14, 4);

        long leastSigBits = decode(str, 19, 4);
        leastSigBits <<= 48;
        leastSigBits |= decode(str, 24, 12);

        return new UUID(mostSigBits, leastSigBits);
    }

    private static long decode(final String str, final int offset, final int length) {
        long curr = 0;
        for (int i = 0; i < length; i++) {
            byte v = CHAR_MAP[str.charAt(i + offset)];
            if (v == -1) {
                throw new IllegalArgumentException("Invalid UUID string: " + str);
            }
            curr |= v;
            curr <<= 4;
        }
        // won't be enough precision for all inputs, but will be enough precision for the inputs we expect.
        curr >>= 4;
        return curr;
    }

    private static void initCharMap(final char a, final char b, final int offset) {
        for (int i = a; i <= b; i++) {
            CHAR_MAP[i] = (byte) ((i - a) + offset);
        }
    }
}