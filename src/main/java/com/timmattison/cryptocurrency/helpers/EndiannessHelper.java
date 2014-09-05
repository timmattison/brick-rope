package com.timmattison.cryptocurrency.helpers;

import java.io.ByteArrayOutputStream;

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

    public static int BytesToShort(byte[] bytes) {
        return BytesToShort(bytes, 0);
    }

    public static int BytesToShort(byte[] bytes, int offset) {
        validateSize(bytes, shortSize);

        long returnValue = ((ToUnsignedByte(bytes[offset + 1]) << 8) | ToUnsignedByte(bytes[offset + 0])) & shortMask;
        return (int) returnValue;
    }

    public static int BytesToInt(byte[] bytes) {
        return BytesToInt(bytes, 0);
    }

    public static int BytesToInt(byte[] bytes, int offset) {
        validateSize(bytes, intSize);

        long returnValue = ((ToUnsignedByte(bytes[offset + 3]) << 24) | (ToUnsignedByte(bytes[offset + 2]) << 16) | (ToUnsignedByte(bytes[offset + 1]) << 8) | (ToUnsignedByte(bytes[offset + 0]))) & intMask;
        return (int) returnValue;
    }

    public static long BytesToLong(byte[] bytes) {
        return BytesToLong(bytes, 0);
    }

    public static long BytesToLong(byte[] bytes, int offset) {
        validateSize(bytes, longSize);

        long returnValue = (ToUnsignedByte(bytes[offset + 7]) << 56) | (ToUnsignedByte(bytes[offset + 6]) << 48) | (ToUnsignedByte(bytes[offset + 5]) << 40) | (ToUnsignedByte(bytes[offset + 4]) << 32) | (ToUnsignedByte(bytes[offset + 3]) << 24) | (ToUnsignedByte(bytes[offset + 2]) << 16) | (ToUnsignedByte(bytes[offset + 1]) << 8) | (ToUnsignedByte(bytes[offset + 0]));
        return returnValue;
    }

    public static byte[] ShortToBytes(short value) {
        byte[] returnValue = new byte[2];

        returnValue[1] = (byte) ((value >> 8) & 0xFF);
        returnValue[0] = (byte) (value & 0xFF);

        return returnValue;
    }

    public static byte[] IntToBytes(int value) {
        byte[] returnValue = new byte[4];

        returnValue[3] = (byte) ((value >> 24) & 0xFF);
        returnValue[2] = (byte) ((value >> 16) & 0xFF);
        returnValue[1] = (byte) ((value >> 8) & 0xFF);
        returnValue[0] = (byte) (value & 0xFF);

        return returnValue;
    }

    public static byte[] LongToBytes(long value) {
        byte[] returnValue = new byte[8];

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

    public static long ToUnsignedByte(byte input) {
        return ((int) input) & 0xFF;
    }

    public static long ToUnsignedValue(byte[] input) {
        if (input == null) {
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        if (input.length == 1) {
            return ToUnsignedByte(input[0]);
        }

        long result = 0;

        for(int loop = 0; loop < input.length; loop++) {
            long currentValue = ToUnsignedByte(input[loop]) << (loop * 8);
            result += currentValue;
        }

        return result;
    }

    private static void validateSize(byte[] bytes, int requiredSize) {
        if (bytes == null) {
            throw new UnsupportedOperationException("Bytes cannot be NULL");
        } else if (bytes.length < requiredSize) {
            throw new UnsupportedOperationException("Need " + requiredSize + " byte(s), saw " + bytes.length + " byte(s)");
        }
    }

    public static byte[] VarIntToBytes(long varInt) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        // How big is the value?
        if (varInt < 0xfd) {
            // Small enough to fit in one byte
            bytes.write((byte) varInt);
        } else if (varInt < 0xffff) {
            // Small enough to fit in two bytes
            bytes.write((byte) 0xfd);
            byte[] temp = ShortToBytes((short) varInt);
            bytes.write(temp, 0, temp.length);
        } else if (varInt < 0xffffffffL) {
            // Small enough to fit in four bytes
            bytes.write((byte) 0xfe);
            byte[] temp = IntToBytes((int) varInt);
            bytes.write(temp, 0, temp.length);
        } else {
            // Needs eight bytes
            bytes.write((byte) 0xff);
            byte[] temp = LongToBytes((int) varInt);
            bytes.write(temp, 0, temp.length);
        }

        return bytes.toByteArray();
    }
}
