package com.hometask;

public class DecrementDecoder implements Decoder {
    public byte[] decode(byte[] data) {
        byte[] result = new byte[data.length];
        for (int i = 0; i < data.length; ++i) {
            result[i] = (byte) (data[i] - 1);
        }
        return result;
    }
}
