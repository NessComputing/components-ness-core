package com.nesscomputing.uuid;

import org.junit.Assert;
import org.junit.Test;

public class TestHexConvert
{
    private char [] numChars = new char [] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
    private char [] alphaLowChars = new char [] { 'a', 'b', 'c', 'd', 'e', 'f' };
    private char [] alphaHiChars = new char [] { 'A', 'B', 'C', 'D', 'E', 'F' };

    private char [] illegalChars = new char [] { '0' - 1, '9' + 1, 'a' - 1, 'f' + 1, 'A' - 1, 'F' + 1 };

    @Test
    public void testNumChars()
    {
        for (int i = 0; i < numChars.length; i++) {
            int res = NessUUID.getNibbleFromChar(numChars[i]);
            Assert.assertEquals(i, res);
        }
    }

    @Test
    public void testAlphaLowChars()
    {
        for (int i = 0; i < alphaLowChars.length; i++) {
            int res = NessUUID.getNibbleFromChar(alphaLowChars[i]);
            Assert.assertEquals(i + 10, res);
        }
    }

    @Test
    public void testAlphaHiChars()
    {
        for (int i = 0; i < alphaHiChars.length; i++) {
            int res = NessUUID.getNibbleFromChar(alphaHiChars[i]);
            Assert.assertEquals(i + 10, res);
        }
    }

    @Test
    public void testBoundaryChars()
    {
        for (int i = 0; i < illegalChars.length; i++) {
            try {
                NessUUID.getNibbleFromChar(illegalChars[i]);
                Assert.fail(illegalChars[i] + " must not be accepted!");
            }
            catch (IllegalArgumentException iae) {
                // ok
            }
        }
    }
}
