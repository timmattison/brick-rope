package com.timmattison.bitcoin.test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 9:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class EndiannessHelper {
    private static final int shortSize = 2;
    private static final long shortMask = 0x000000FFFFL;
    private static final int intSize = 4;
    private static final long intMask = 0x00FFFFFFFFL;
    private static final int longSize = 4;

    public static short BytesToShort(Byte[] bytes) {
        validateSize(bytes, shortSize);

        long returnValue = ((toRealByte(bytes[1]) << 8) | toRealByte(bytes[0])) & shortMask;
        return (short) returnValue;
    }

    public static int BytesToInt(Byte[] bytes) {
        validateSize(bytes, intSize);

        long returnValue = ((toRealByte(bytes[3]) << 24) | (toRealByte(bytes[2]) << 16) | (toRealByte(bytes[1]) << 8) | (toRealByte(bytes[0]))) & intMask;
        return (int) returnValue;
    }

    public static long BytesToLong(Byte[] bytes) {
        validateSize(bytes, longSize);

        long returnValue = (toRealByte(bytes[7]) << 56) | (toRealByte(bytes[6]) << 48) | (toRealByte(bytes[5]) << 40) | (toRealByte(bytes[4]) << 32) | (toRealByte(bytes[3]) << 24) | (toRealByte(bytes[2]) << 16) | (toRealByte(bytes[1]) << 8) | (toRealByte(bytes[0]));
        return returnValue;
    }

    public static Byte[] ShortToBytes(short value) {
        Byte[] returnValue = new Byte[2];

        returnValue[1] = (byte) ((value >> 8) & 0xFF);
        returnValue[0] = (byte) (value & 0xFF);

        return returnValue;
    }

    public static Byte[] IntToBytes(int value) {
        Byte[] returnValue = new Byte[4];

        returnValue[3] = (byte) ((value >> 24) & 0xFF);
        returnValue[2] = (byte) ((value >> 16) & 0xFF);
        returnValue[1] = (byte) ((value >> 8) & 0xFF);
        returnValue[0] = (byte) (value & 0xFF);

        return returnValue;
    }

    public static Byte[] LongToBytes(long value) {
        Byte[] returnValue = new Byte[8];

        returnValue[7] = (byte) ((value >> 56) & 0xFF);
        returnValue[6] = (byte) ((value >> 48) & 0xFF);
        returnValue[5] = (byte) ((value >> 40) & 0xFF);
        returnValue[4] = (byte) ((value >> 32) & 0xFF);
        returnValue[3] = (byte) ((value >> 24) & 0xFF);
        returnValue[2] = (byte) ((value >> 16) & 0xFF);
        returnValue[1] = (byte) ((value >> 8) & 0xFF);
        returnValue[0] = (byte) (value & 0xFF);

        return returnValue;
    }

    private static long toRealByte(byte input) {
        return ((int) input) & 0xFF;
    }

    private static void validateSize(Byte[] bytes, int requiredSize) {
        if (bytes == null) {
            throw new UnsupportedOperationException("Bytes cannot be NULL");
        } else if (bytes.length < requiredSize) {
            throw new UnsupportedOperationException("Need " + requiredSize + " byte(s), saw " + bytes.length + " byte(s)");
        }
    }

    public static Byte[] VarIntToBytes(long varInt) {
        List<Byte> bytes = new ArrayList<Byte>();
        // How big is the value?
        if (varInt < 0xfd) {
            // Small enough to fit in one byte
            bytes.add((byte) varInt);
        } else if (varInt < 0xffff) {
            // Small enough to fit in two bytes
            bytes.add((byte) 0xfd);
            ByteArrayHelper.addBytes(bytes, ShortToBytes((short) varInt));
        } else if (varInt < 0xffffffffL) {
            // Small enough to fit in four bytes
            bytes.add((byte) 0xfe);
            ByteArrayHelper.addBytes(bytes, IntToBytes((int) varInt));
        } else {
            // Needs eight bytes
            bytes.add((byte) 0xff);
            ByteArrayHelper.addBytes(bytes, IntToBytes((int) varInt));
        }

        return bytes.toArray(new Byte[bytes.size()]);
    }
}
