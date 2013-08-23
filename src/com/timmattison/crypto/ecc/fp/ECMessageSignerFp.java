package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.ECHelper;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignerFp implements ECCMessageSigner {
    private ECCSignatureFactory eccSignatureFactory;
    private ECCMessageSignerDigestFactory eccMessageSignerDigestFactory;
    private BigIntegerRandom bigIntegerRandom;
    private ECCKeyPair eccKeyPair;

    public ECMessageSignerFp() {

    }

    @AssistedInject
    public ECMessageSignerFp(ECCSignatureFactory eccSignatureFactory, ECCMessageSignerDigestFactory eccMessageSignerDigestFactory, @Assisted("bigIntegerRandom") BigIntegerRandom bigIntegerRandom, @Assisted("eccKeyPair") ECCKeyPair eccKeyPair) {
        this.eccSignatureFactory = eccSignatureFactory;
        this.eccMessageSignerDigestFactory = eccMessageSignerDigestFactory;
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

        // Hash the message with SHA-1
        MessageDigest md = eccMessageSignerDigestFactory.create();
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(md.digest());

        // Calculate e
        XXX This is broken!
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
