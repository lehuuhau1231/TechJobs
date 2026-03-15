package com.lhh.techjobs.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class VectorService {
    public static byte[] floatArrayToBytes(float[] array) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * array.length);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (float v : array) buffer.putFloat(v);
        return buffer.array();
    }
}
