package com.github.itoshkov.logisimn2t;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BitUtilsTest {
    @Test
    public void testInvertAndMirror() {
        for (int i = 0; i <= 0xffff; i++) {
            final int im = invertAndMirror(i);
            final int im2 = invertAndMirror(im);
            assertEquals(i, im2);
        }
    }

    private int invertAndMirror(int i) {
        byte b0 = (byte) ((i >>> 8) & 0xff);
        byte b1 = (byte) (i & 0xff);
        return BitUtils.invertAndMirror(b0, b1);
    }
}