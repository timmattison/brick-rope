package com.timmattison.cryptocurrency.helpers;

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

    public static byte[] concatenate(byte[] input1, byte[] input2) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        output.write(input1);
        output.write(input2);

        return output.toByteArray();
    }

    /**
     * Reverse a byte array.  Typically used when endianness is backwards and we're converting to BigIntegers.
     * @param input
     * @return
     */
    public static byte[] reverseBytes(byte[] input) {
        if(input == null) {
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        byte[] returnValue = new byte[input.length];

        for(int loop = 0; loop < input.length; loop++) {
            returnValue[loop] = input[input.length - loop - 1];
        }

        return returnValue;
    }

    public static int indexOf(byte[] needle, byte[] haystack) {
        for(int outerLoop = 0; outerLoop < haystack.length; outerLoop++) {
            boolean matching = true;

            for(int innerLoop = 0; (innerLoop < needle.length) && (matching); innerLoop++) {
                if(haystack[outerLoop + innerLoop] != needle[innerLoop]) {
                    matching = false;
                }
            }

            // Was this a match?
            if(matching)
            {
                // Yes, return the index
                return outerLoop;
            }
        }

        // No match
        return -1;
    }

    public static String toHex(byte[] input) {
        StringBuilder stringBuilder = new StringBuilder();

        for(byte value : input) {
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
}
