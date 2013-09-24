package com.timmattison.cryptocurrency.standard.hashing.tests;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 9:52 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashTestUtilities {
    public static String generateRepetitiveString(String input, int count) {
        StringBuilder stringBuilder = new StringBuilder();

        for (int loop = 0; loop < count; loop++) {
            stringBuilder.append(input);
        }

        return stringBuilder.toString();
    }

    public static void testXChars(HashTester hashTester, String implementationName, int count) throws Exception {
        StringBuilder stringBuilder = new StringBuilder();

        for (int loop = 0; loop < count; loop++) {
            stringBuilder.append("a");
        }

        String inputString = stringBuilder.toString();
        byte[] inputBytes = inputString.getBytes();

        MessageDigest digest = MessageDigest.getInstance(implementationName);
        digest.update(inputBytes);
        byte[] hash = digest.digest();
        String hashHex = new BigInteger(1, hash).toString(16);

        if (hashHex.length() != 32) {
            stringBuilder = new StringBuilder();

            for (int loop = hashHex.length(); loop < 32; loop++) {
                stringBuilder.append("0");
            }

            stringBuilder.append(hashHex);

            hashHex = stringBuilder.toString();
        }

        HashTestVector testVector = new HashTestVector(hashTester, inputString, hashHex);
        testVector.runTest();
    }
}
