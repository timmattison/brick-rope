package com.timmattison.cryptocurrency.helpers;

import com.timmattison.cryptocurrency.standard.hashing.Endianness;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/12/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayHelper {
    private static final Logger logger = Logger.getLogger(ByteArrayHelper.class.getName());

    public static String toHex(Byte input) {
        return String.format("%02x", input);
    }

    public static String formatArray(byte[] array) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int loop = 0; loop < array.length; loop++) {
            stringBuilder.append(toHex(array[loop]) + " ");
        }

        return stringBuilder.toString();
    }

    private static void partialCompare(Byte[] array1, Byte[] array2) {
        if ((array1 == null) || (array2 == null)) {
            throw new UnsupportedOperationException("Neither of the arrays can be NULL");
        }

        int length = array1.length < array2.length ? array1.length : array2.length;

        for (int loop = 0; loop < length; loop++) {
            if (!array1[loop].equals(array2[loop])) {
                logger.info("Arrays differ at byte " + loop + " [" + ByteArrayHelper.toHex(array1[loop]) + "][" + ByteArrayHelper.toHex(array2[loop]) + "]");
                return;
            }
        }

        logger.info("Arrays are equal up to byte " + length);
    }

    public static byte[] concatenate(byte[] input1, byte[] input2) {
        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();

            output.write(input1);
            output.write(input2);

            return output.toByteArray();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Reverse a byte array.  Typically used when endianness is backwards and we're converting to BigIntegers.
     *
     * @param input
     * @return
     */
    public static byte[] reverseBytes(byte[] input) {
        if (input == null) {
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        byte[] returnValue = new byte[input.length];

        for (int loop = 0; loop < input.length; loop++) {
            returnValue[loop] = input[input.length - loop - 1];
        }

        return returnValue;
    }

    public static int indexOf(byte[] needle, byte[] haystack) {
        for (int outerLoop = 0; outerLoop < haystack.length; outerLoop++) {
            boolean matching = true;

            for (int innerLoop = 0; (innerLoop < needle.length) && (matching); innerLoop++) {
                if (haystack[outerLoop + innerLoop] != needle[innerLoop]) {
                    matching = false;
                }
            }

            // Was this a match?
            if (matching) {
                // Yes, return the index
                return outerLoop;
            }
        }

        // No match
        return -1;
    }

    public static String toHex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder();

        for (byte value : input) {
            stringBuilder.append(String.format("%02x", value));
        }

        return stringBuilder.toString();
    }

    public static int get32BitWord(byte[] input, int offset) {
        byte byte0 = input[(offset * 4) + 0];
        byte byte1 = input[(offset * 4) + 1];
        byte byte2 = input[(offset * 4) + 2];
        byte byte3 = input[(offset * 4) + 3];

        int output = ((byte3 << 24) & 0xFF000000) | ((byte2 << 16) & 0x00FF0000) | ((byte1 << 8) & 0x0000FF00) | (byte0 & 0x000000FF);

        return output;
    }

    public static byte[] appendBit(byte[] input, int numberOfBits, boolean bit) {
        // TODO - Optimize this!
        byte[] localInput = input;

        // Do we need to add a new byte?
        if (((numberOfBits % 8) == 0) && ((numberOfBits / 8) >= input.length)) {
            // Yes, add a byte to the input
            byte[] tempInput = new byte[localInput.length + 1];

            for (int loop = 0; loop < localInput.length; loop++) {
                tempInput[loop] = localInput[loop];
            }

            // Make the last byte zero
            tempInput[localInput.length] = 0;

            localInput = tempInput;
        }

        localInput = setBit(localInput, numberOfBits, bit);

        return localInput;
    }

    public static byte[] setBit(byte[] input, int bitToSet, boolean bit) {
        byte[] localInput = input;

        // Where do we add the new bit?
        int position = bitToSet / 8;
        bitToSet %= 8;
        int bitMask = 1 << (7 - bitToSet);

        if (bit == true) {
            // Just OR in the bit
            localInput[position] |= bitMask;
        } else {
            // Invert the bits
            bitMask = ~bitMask;

            // AND out the bit
            localInput[position] &= bitMask;
        }

        return localInput;
    }

    public static byte[] padBits(byte[] input, int inputLength, int outputLength, boolean bit) {
        byte[] localInput = input;

        if (inputLength != outputLength) {
            // TODO - Optimize this!  Super slow!
            for (int loop = inputLength; loop < outputLength; loop++) {
                localInput = appendBit(localInput, loop, bit);
            }
        }

        return localInput;
    }

    public static byte[] append64Bits(Endianness endianness, byte[] input, int numberOfBits, long value) {
        if (endianness == Endianness.BigEndian) {
            return append64BitsBigEndian(input, numberOfBits, value);
        } else {
            return append64BitsLittleEndian(input, numberOfBits, value);
        }
    }

    private static byte[] append64BitsBigEndian(byte[] input, int numberOfBits, long value) {
        if ((numberOfBits % 8) != 0) {
            throw new UnsupportedOperationException("I am lazy, not supported yet");
        }

        byte[] bytesToAppend = new byte[8];
        bytesToAppend[7] = (byte) (value & 0x00FFL);
        bytesToAppend[6] = (byte) ((value >>> 8) & 0x00FFL);
        bytesToAppend[5] = (byte) ((value >>> 16) & 0x00FFL);
        bytesToAppend[4] = (byte) ((value >>> 24) & 0x00FFL);
        bytesToAppend[3] = (byte) ((value >>> 32) & 0x00FFL);
        bytesToAppend[2] = (byte) ((value >>> 40) & 0x00FFL);
        bytesToAppend[1] = (byte) ((value >>> 48) & 0x00FFL);
        bytesToAppend[0] = (byte) ((value >>> 56) & 0x00FFL);

        byte[] returnValue = new byte[input.length + 8];

        for (int loop = 0; loop < input.length; loop++) {
            returnValue[loop] = input[loop];
        }

        for (int loop = 0; loop < 8; loop++) {
            returnValue[input.length + loop] = bytesToAppend[loop];
        }

        return returnValue;
    }

    private static byte[] append64BitsLittleEndian(byte[] input, int numberOfBits, long value) {
        if ((numberOfBits % 8) != 0) {
            throw new UnsupportedOperationException("I am lazy, not supported yet");
        }

        byte[] bytesToAppend = new byte[8];
        bytesToAppend[0] = (byte) (value & 0x00FFL);
        bytesToAppend[1] = (byte) ((value >>> 8) & 0x00FFL);
        bytesToAppend[2] = (byte) ((value >>> 16) & 0x00FFL);
        bytesToAppend[3] = (byte) ((value >>> 24) & 0x00FFL);
        bytesToAppend[4] = (byte) ((value >>> 32) & 0x00FFL);
        bytesToAppend[5] = (byte) ((value >>> 40) & 0x00FFL);
        bytesToAppend[6] = (byte) ((value >>> 48) & 0x00FFL);
        bytesToAppend[7] = (byte) ((value >>> 56) & 0x00FFL);

        byte[] returnValue = new byte[input.length + 8];

        for (int loop = 0; loop < input.length; loop++) {
            returnValue[loop] = input[loop];
        }

        for (int loop = 0; loop < 8; loop++) {
            returnValue[input.length + loop] = bytesToAppend[loop];
        }

        return returnValue;
    }
}
