package com.timmattison.cryptocurrency.ecc.fp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 12:14 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECHelperFp {
    public static String toBitStringFromHexString(String input) {
        if ((input.length() % 2) != 0) {
            throw new UnsupportedOperationException("This doesn't appear to be a properly padding hex string.  The length (mod 2) isn't zero.");
        }

        BigInteger temp = new BigInteger(input, 16);

        StringBuilder stringBuilder = new StringBuilder();

        // Loop through the bits
        for (int loop = 0; loop < temp.bitLength(); loop++) {
            stringBuilder.append(temp.testBit(temp.bitLength() - loop - 1) ? "1" : "0");
        }

        return stringBuilder.toString();
    }

    public static String toHexStringFromBitString(String input) {
        if ((input.length() % 8) != 0) {
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

    public static BigInteger calculateS(ECKeyPairFp keyPair, BigInteger k, BigInteger e, BigInteger r) {
        return calculateS(k, keyPair.getN(), e, keyPair.getD(), r);
    }

    private static BigInteger calculateS(BigInteger k, BigInteger n, BigInteger e, BigInteger dU, BigInteger r) {
        return k.modPow(BigInteger.ONE.negate(), n).multiply(e.add(dU.multiply(r))).mod(n);
    }

    public static BigInteger calculateE(X9ECParameters x9ECParameters, String hashHexString, byte[] hashBytes) {
        return calculateE(x9ECParameters.getN(), hashHexString, hashBytes);
    }

    public static BigInteger calculateE(ECKeyPairFp keyPair, String hashHexString, byte[] hashBytes) {
        return calculateE(keyPair.getN(), hashHexString, hashBytes);
    }

    private static BigInteger calculateE(BigInteger n, String hashHexString, byte[] hashBytes) {
        // If the ceiling of log_2 n >= (hashlen * 8) then e = H
        // Otherwise set e = leftmost log_2 n bits of H

        int logBase2OfN = n.bitLength();

        BigInteger e;

        if (logBase2OfN >= (hashBytes.length * 8)) {
            e = new BigInteger(hashHexString, 16);
        } else {
            // TODO - Not implementing this yet
            throw new UnsupportedOperationException("Not implemented yet");
        }

        return e;
    }
}
