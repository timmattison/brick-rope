package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.*;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignerFp implements ECCMessageSigner {
    private final ECCSignatureFactory eccSignatureFactory;
    private final ECCMessageSignerDigestFactory ECCMessageSignerDigestFactory;
    private final Random random;
    private final ECCKeyPair eccKeyPair;

    @AssistedInject
    public ECMessageSignerFp(ECCSignatureFactory eccSignatureFactory, ECCMessageSignerDigestFactory ECCMessageSignerDigestFactory, @Assisted("keyPair") ECCKeyPair eccKeyPair, @Assisted("random") Random random) {
        this.eccSignatureFactory = eccSignatureFactory;
        this.ECCMessageSignerDigestFactory = ECCMessageSignerDigestFactory;
        this.random = random;
        this.eccKeyPair = eccKeyPair;
    }

    public ECCSignature signMessage(byte[] messageBytes) throws Exception {
        // Select a random k value that has the same number of bits as P
        BigInteger k = new BigInteger((int) (Math.log(eccKeyPair.getECCParameters().getCurve().getP().doubleValue()) / Math.log(2)), random);

        // Get the mod n value of k
        k = k.mod(eccKeyPair.getN());

        // Compute R = (xR, yR) = k * G
        ECCPoint R = eccKeyPair.getG().multiply(k);

        // Derive an integer r from xR (mod n)
        BigInteger r = R.getX().toBigInteger().mod(eccKeyPair.getN());

        // Is r zero?
        if (r.equals(BigInteger.ZERO)) {
            // No, throw an exception
            throw new Exception("r cannot be zero");
        }

        // Hash the message with SHA-1
        MessageDigest md = ECCMessageSignerDigestFactory.create();
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(md.digest());

        // Calculate e
        BigInteger e = ECHelper.calculateE(eccKeyPair, H, hashBytes);

        // Compute s -  s = k^-1(e + dU * r) (mod n)
        BigInteger s = ECHelper.calculateS(eccKeyPair, k, e, r);

        // Is it zero (mod n)?
        if (s.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new Exception("s cannot be zero (mod n)");
        }

        return eccSignatureFactory.create(eccKeyPair.getECCParameters(), r, s, eccKeyPair.getQ());
    }
}
