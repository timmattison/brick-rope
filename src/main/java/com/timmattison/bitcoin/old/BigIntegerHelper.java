package com.timmattison.bitcoin.old;

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
}
