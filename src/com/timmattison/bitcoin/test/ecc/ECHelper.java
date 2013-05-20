package com.timmattison.bitcoin.test.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECHelper {
    public static String toBitStringFromHexString(String input) {
        if((input.length() % 2) != 0) {
            throw new UnsupportedOperationException("This doesn't appear to be a properly padding hex string.  The length (mod 2) isn't zero.");
        }

        BigInteger temp = new BigInteger(input, 16);

        StringBuilder stringBuilder = new StringBuilder();

        // Loop through the bits
        for(int loop = 0; loop < temp.bitLength(); loop++) {
            stringBuilder.append(temp.testBit(temp.bitLength() - loop - 1) ? "1" : "0");
        }

        return stringBuilder.toString();
    }

    public static String toHexStringFromBitString(String input) {
        if((input.length() % 8) != 0) {
            throw new UnsupportedOperationException("This doesn't appear to be a properly padded binary string.  The length (mod 8) isn't zero.");
        }

        BigInteger temp = new BigInteger(input, 2);

        return temp.toString(16);
    }

    public static boolean compare(String first, String second) {
        // Convert to lowercase and remove all spaces
        first = first.toLowerCase().replaceAll(" ", "");
        second = second.toLowerCase().replaceAll(" ", "");

        return first.equals(second);
    }
}
