package com.hometask;

import java.util.Arrays;

public class ByteUtils {
    public static boolean bytesStartWith(byte[] bytes, String prefix) {
        return prefix.equals(new String(Arrays.copyOfRange(bytes, 0, prefix.length())));
    }
}
