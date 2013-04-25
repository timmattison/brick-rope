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
    public static ArrayList<Byte> removeFirstByte(List<Byte> list) {
        return new ArrayList<Byte>(list.subList(1, list.size()));
    }

    public static ArrayList<Byte> grabHeadBytes(List<Byte> list, int count) {
        return new ArrayList<Byte>(list.subList(0, count));
    }

    public static ArrayList<Byte> grabTailBytes(List<Byte> list, int count) {
        return new ArrayList<Byte>(list.subList(count, list.size()));
    }

    public static void addBytes(List<Byte> list, Byte[] bytes) {
        for (int loop = 0; loop < bytes.length; loop++) {
            list.add(bytes[loop]);
        }
    }

    public static byte[] JavaLangByteArrayToByteArray(Byte[] bytes) {
        byte[] returnValue = new byte[bytes.length];

        for (int loop = 0; loop < bytes.length; loop++) {
            returnValue[loop] = bytes[loop];
        }

        return returnValue;
    }

    public static byte[] JavaLangByteListToByteArray(List<Byte> bytes) {
        Byte[] tempByteArray = bytes.toArray(new Byte[bytes.size()]);

        return JavaLangByteArrayToByteArray(tempByteArray);
    }

    public static Byte[] ByteArrayToJavaLangByteArray(byte[] bytes) {
        Byte[] returnValue = new Byte[bytes.length];

        for (int loop = 0; loop < bytes.length; loop++) {
            returnValue[loop] = bytes[loop];
        }

        return returnValue;
    }

    public static List<Byte> JavaLangByteArrayToByteList(Byte[] bytes) {
        return Arrays.asList(bytes);
    }

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
}
