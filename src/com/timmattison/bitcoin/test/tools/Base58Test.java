package com.timmattison.bitcoin.test.tools;

import com.timmattison.bitcoin.test.HashHelper;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/21/13
 * Time: 5:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class Base58Test {
    // Example from: http://bitcoin.stackexchange.com/questions/8247/how-can-i-convert-a-sha256-hash-into-a-bitcoin-base58-private-key
    private static final byte leadingByte = (byte) 0x80;
    private static final byte[] privateKey = new byte[]{
            0x75, 0x42, (byte) 0xFB, 0x66, (byte) 0x85, (byte) 0xF9, (byte) 0xFD, (byte) 0x8F, 0x37, (byte) 0xD5, 0x6F, (byte) 0xAF, 0x62, (byte) 0xF0, (byte) 0xBB,
            0x45, 0x63, 0x68, 0x4A, 0x51, 0x53, (byte) 0x9E, 0x4B, 0x26, (byte) 0xF0, (byte) 0x84, 0x0D, (byte) 0xB3, 0x61, (byte) 0xE0, 0x02, 0x7C
    };

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        String result = Base58Encoder.base58Encode(leadingByte, privateKey);

        byte[] ripemd160Hash = HashHelper.ripemd160Hash(result.getBytes());
        result = "";
    }
}
