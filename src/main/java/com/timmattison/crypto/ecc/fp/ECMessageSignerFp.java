package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Hash;

import javax.inject.Inject;
import java.math.BigInteger;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignerFp implements ECCMessageSigner {
    private ECCSignatureFactory eccSignatureFactory;
    private ECCMessageSignerHashFactory eccMessageSignerHashFactory;
    private BigIntegerRandom bigIntegerRandom;
    private ECCKeyPair eccKeyPair;

    public ECMessageSignerFp() {
    }

    @Inject
    public ECMessageSignerFp(ECCSignatureFactory eccSignatureFactory, ECCMessageSignerHashFactory eccMessageSignerHashFactory, @Assisted("bigIntegerRandom") BigIntegerRandom bigIntegerRandom, @Assisted("eccKeyPair") ECCKeyPair eccKeyPair) {
        this.eccSignatureFactory = eccSignatureFactory;
        this.eccMessageSignerHashFactory = eccMessageSignerHashFactory;
        this.bigIntegerRandom = bigIntegerRandom;
        this.eccKeyPair = eccKeyPair;
    }

    @Override
    public ECCSignature signMessage(byte[] messageBytes) {
        // Select a random k value that has the same number of bits as P
        BigInteger k = bigIntegerRandom.getNext(eccKeyPair.getECCParameters().getCurve().getP());

        // Get the mod n value of k
        k = k.mod(eccKeyPair.getN());

        ECCPoint G = eccKeyPair.getG();

        // Compute R = (xR, yR) = k * G
        ECCPoint R = G.multiply(k);

        // Derive an integer r from xR (mod n)
        BigInteger r = R.getX().toBigInteger().mod(eccKeyPair.getN());

        // Is r zero?
        if (r.equals(BigInteger.ZERO)) {
            // No, throw an exception
            throw new UnsupportedOperationException("r cannot be zero");
        }

        // Hash the message
        Hash hash = eccMessageSignerHashFactory.create(messageBytes);
        byte[] hashBytes = hash.getOutput();
        String H = ByteArrayHelper.toHex(hashBytes);

        // Calculate e
        BigInteger e = ECHelper.calculateE(eccKeyPair, H, hashBytes);

        // Compute s -  s = k^-1(e + dU * r) (mod n)
        BigInteger s = ECHelper.calculateS(eccKeyPair, k, e, r);

        // Is it zero (mod n)?
        if (s.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new UnsupportedOperationException("s cannot be zero (mod n)");
        }

        return eccSignatureFactory.create(eccKeyPair.getECCParameters(), r, s, eccKeyPair.getQ());
    }
}
