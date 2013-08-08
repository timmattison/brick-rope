package com.timmattison.cryptocurrency.standard.tests;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 11:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestHelper {
    public static byte[] fromHexStringWrong(String string, boolean changeEndianness) {
        if ((string.length() % 2) != 0)
            throw new IllegalArgumentException("Input string must contain an even number of characters");

        if (changeEndianness) {
            string = changeEndianness(string);
        }

        final byte result[] = new byte[string.length() / 2];
        final char enc[] = string.toCharArray();
        for (int i = 0; i < enc.length; i += 2) {
            StringBuilder curr = new StringBuilder(2);
            curr.append(enc[i]).append(enc[i + 1]);
            result[i / 2] = (byte) Integer.parseInt(curr.toString(), 16);
        }
        return result;
    }

    public static byte[] fromBigEndianHexString(String string) {
        return fromHexString(string, true);
    }

    public static byte[] fromLittleEndianHexString(String string) {
        return fromHexString(string, false);
    }

    private static byte[] fromHexString(String string, boolean changeEndianness) {
        if (changeEndianness) {
            string = changeEndianness(string);
        }

        BigInteger bigInteger = new BigInteger(string, 16);
        byte[] result = bigInteger.toByteArray();

        if(result[0] == 0) {
            return Arrays.copyOfRange(result, 1, result.length);
        }
        else {
            return result;
        }
    }

    private static String changeEndianness(String string) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int loop = 0; loop < string.length() / 2; loop++) {
            int startIndex = string.length() - ((loop + 1) * 2);
            int endIndex = startIndex + 2;
            String substring = string.substring(startIndex, endIndex);
            stringBuilder.append(substring);
        }

        return stringBuilder.toString();
    }
}
