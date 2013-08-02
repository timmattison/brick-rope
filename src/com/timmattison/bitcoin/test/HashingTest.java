package com.timmattison.bitcoin.test;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: Tim-Win7
 * Date: 4/20/13
 * Time: 3:21 PM
 * To change this template use File | Settings | File Templates.
 */
public class HashingTest {
    static byte[] testBlock1 = new byte[]{
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Block format
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Hash of previous block
            (byte) 0x3b, (byte) 0xa3, (byte) 0xed, (byte) 0xfd,
            (byte) 0x7a, (byte) 0x7b, (byte) 0x12, (byte) 0xb2,
            (byte) 0x7a, (byte) 0xc7, (byte) 0x2c, (byte) 0x3e,
            (byte) 0x67, (byte) 0x76, (byte) 0x8f, (byte) 0x61,
            (byte) 0x7f, (byte) 0xc8, (byte) 0x1b, (byte) 0xc3,
            (byte) 0x88, (byte) 0x8a, (byte) 0x51, (byte) 0x32,
            (byte) 0x3a, (byte) 0x9f, (byte) 0xb8, (byte) 0xaa,
            (byte) 0x4b, (byte) 0x1e, (byte) 0x5e, (byte) 0x4a, // Merkle root
            (byte) 0x29, (byte) 0xab, (byte) 0x5f, (byte) 0x49, // Timestamp
            (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x1d, // Bits
            (byte) 0x1d, (byte) 0xac, (byte) 0x2b, (byte) 0x7c, // Nonce
    };

    public static void main(String[] args) throws NoSuchAlgorithmException {
        MessageDigest md1 = MessageDigest.getInstance("SHA-256");
        MessageDigest md2 = MessageDigest.getInstance("SHA-256");

        md1.update(testBlock1);
        md2.update(md1.digest());

        byte[] result = md2.digest();
        ByteArrayHelper.printArray(result);
    }
}
