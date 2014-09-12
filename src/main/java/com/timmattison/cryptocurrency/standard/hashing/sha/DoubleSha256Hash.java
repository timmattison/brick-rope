package com.timmattison.cryptocurrency.standard.hashing.sha;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.cryptocurrency.bitcoin.Constants;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Hash;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DoubleSha256Hash implements Hash {
    private final byte[] input;
    private byte[] output;
    private BigInteger outputBigInteger;

    @Inject
    public DoubleSha256Hash(@Assisted("input") byte[] input) {
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

    @Override
    public BigInteger getOutputBigInteger() {
        if (outputBigInteger == null) {
            outputBigInteger = new BigInteger(ByteArrayHelper.reverseBytes(getOutput()));
        }

        return outputBigInteger;
    }
}
