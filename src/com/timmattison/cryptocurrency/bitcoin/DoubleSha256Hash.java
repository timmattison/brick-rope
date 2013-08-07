package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.script.Constants;
import com.timmattison.cryptocurrency.interfaces.Hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DoubleSha256Hash implements Hash {
    private final byte[] input;
    private byte[] output;

    public DoubleSha256Hash(byte[] input) {
        this.input = input;
    }

    @Override
    public byte[] getInput() {
        return input;
    }

    @Override
    public byte[] getOutput() {
        if (output == null) {
            try {
                MessageDigest messageDigest1 = MessageDigest.getInstance(Constants.SHA256_ALGORITHM);

                MessageDigest messageDigest2 = MessageDigest.getInstance(Constants.SHA256_ALGORITHM);

                output = messageDigest2.digest(messageDigest1.digest(input));
            } catch (NoSuchAlgorithmException e) {
                throw new UnsupportedOperationException(e);
            }
        }

        return output;
    }
}
