package com.timmattison.bitcoin.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/12/13
 * Time: 11:07 AM
 * To change this template use File | Settings | File Templates.
 */
public class ByteArrayHelper {
    public static void printArray(byte[] array) {
        System.out.println(formatArray(array));
    }

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
                System.out.println("Arrays differ at byte " + loop + " [" + ByteArrayHelper.toHex(array1[loop]) + "][" + ByteArrayHelper.toHex(array2[loop]) + "]");
                return;
            }
        }

        System.out.println("Arrays are equal up to byte " + length);
    }
}
