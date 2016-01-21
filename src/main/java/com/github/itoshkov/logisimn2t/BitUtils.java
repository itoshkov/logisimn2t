package com.github.itoshkov.logisimn2t;

public class BitUtils {
    public static int invertAndMirror(byte p0, byte p1) {
        final int s0 = (p0 << 24) & 0xff000000;
        final int s1 = (p1 << 16) & 0x00ff0000;
        final int out = s0 | s1;
        return Integer.reverse(~out) & 0xffff;
    }
}
