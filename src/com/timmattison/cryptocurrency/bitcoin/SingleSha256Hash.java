package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.script.Constants;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Hash;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SingleSha256Hash implements Hash {
    private final byte[] input;
    private byte[] output;
    private BigInteger outputBigInteger;

    public SingleSha256Hash(byte[] input) {
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

                output = messageDigest1.digest(input);
            } catch (NoSuchAlgorithmException e) {
                throw new UnsupportedOperationException(e);
            }
        }

        return output;
    }

    @Override
    public BigInteger getOutputBigInteger() {
        if (outputBigInteger == null) {
            outputBigInteger = new BigInteger(ByteArrayHelper.reverseBytes(getOutput()));
        }

        return outputBigInteger;
    }
}
