package com.timmattison.bitcoin.old;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/2/13
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashHelper {
    private static final String sha256HashName = "SHA-256";
    private static final String ripemd160HashName = "RIPEMD-160";

    public static byte[] doubleSha256Hash(List<byte[]> inputs) throws NoSuchAlgorithmException {
        // Get two message digest objects so we can do the two step hash
        MessageDigest hashOfData = MessageDigest.getInstance(sha256HashName);
        MessageDigest hashOfHash = MessageDigest.getInstance(sha256HashName);

        // Hash all of the data
        for (byte[] input : inputs) {
            hashOfData.update(input);
        }

        // Hash the hashed data
        hashOfHash.update(hashOfData.digest());

        // Get the result and return it
        byte[] result = hashOfHash.digest();

        // Return it
        return result;
    }

    public static byte[] doubleSha256Hash(byte[] input) throws NoSuchAlgorithmException {
        List<byte[]> singleInput = new ArrayList<byte[]>();
        singleInput.add(input);
        return doubleSha256Hash(singleInput);
    }

    public static byte[] ripemd160Hash(byte[] input) throws NoSuchAlgorithmException {
        // Get two message digest objects so we can do the two step hash
        MessageDigest hashOfData = MessageDigest.getInstance(ripemd160HashName);

        // Hash all of the data
        hashOfData.update(input);

        // Get the result and return it
        byte[] result = hashOfData.digest();

        // Return it
        return result;
    }

}
