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

    public static short BytesToShort(byte[] bytes) {
        validateSize(bytes, shortSize);

        long returnValue = ((ToRealByte(bytes[1]) << 8) | ToRealByte(bytes[0])) & shortMask;
        return (short) returnValue;
    }

    public static int BytesToInt(byte[] bytes) {
        validateSize(bytes, intSize);

        long returnValue = ((ToRealByte(bytes[3]) << 24) | (ToRealByte(bytes[2]) << 16) | (ToRealByte(bytes[1]) << 8) | (ToRealByte(bytes[0]))) & intMask;
        return (int) returnValue;
    }

    public static long BytesToLong(byte[] bytes) {
        validateSize(bytes, longSize);

        long returnValue = (ToRealByte(bytes[7]) << 56) | (ToRealByte(bytes[6]) << 48) | (ToRealByte(bytes[5]) << 40) | (ToRealByte(bytes[4]) << 32) | (ToRealByte(bytes[3]) << 24) | (ToRealByte(bytes[2]) << 16) | (ToRealByte(bytes[1]) << 8) | (ToRealByte(bytes[0]));
        return returnValue;
    }

    public static long BytesToValue(byte[] bytes) {
        if(bytes == null) {
            throw new UnsupportedOperationException("Bytes cannot be NULL");
        }

        if(bytes.length == 1) {
            return ToRealByte(bytes);
        }
        else if(bytes.length == 2) {
            return BytesToShort(bytes);
        }
        else if(bytes.length == 3) {
            byte[] tempBytes = new byte[longSize];

            for(int loop = 0; loop < longSize; loop++) {
                tempBytes[loop] = 0;
            }

            tempBytes[0] = bytes[0];
            tempBytes[1] = bytes[1];
            tempBytes[2] = bytes[2];

            return BytesToInt(tempBytes);
        }
        else if(bytes.length == 4) {
            return BytesToLong(bytes);
        }
        else {
            throw new UnsupportedOperationException("Bytes is longer than " + longSize + ".  This is not yet supported.");
        }
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

    public static long ToRealByte(byte input) {
        return ((int) input) & 0xFF;
    }

    public static long ToRealByte(byte[] input) {
        if(input == null) {
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        if(input.length != 1) {
            throw new UnsupportedOperationException("Input length must be 1, saw " + input.length);
        }

        return ((int) input[0]) & 0xFF;
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
