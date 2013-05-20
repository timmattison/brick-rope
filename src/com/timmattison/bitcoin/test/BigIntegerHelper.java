package com.timmattison.bitcoin.test;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class BigIntegerHelper {
    public static BigInteger squareBigInteger(BigInteger input) {
        return input.multiply(input);
    }

    public static boolean compare(String first, String second) {
        // Convert to lowercase and remove all spaces
        first = first.toLowerCase().replaceAll(" ", "");
        second = second.toLowerCase().replaceAll(" ", "");

        return first.equals(second);
    }
}
