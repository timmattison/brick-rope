package com.timmattison.crypto.ecc.fp;

import com.google.inject.Inject;
import com.timmattison.crypto.ecc.ECHelper;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignerDigestFactory;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
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
public class ECMessageSignatureVerifierFp implements ECCMessageSignatureVerifier {
    private final ECCMessageSignerDigestFactory eccMessageSignerDigestFactory;

    @Inject
    public ECMessageSignatureVerifierFp(ECCMessageSignerDigestFactory eccMessageSignerDigestFactory) {
        this.eccMessageSignerDigestFactory = eccMessageSignerDigestFactory;
    }

    @Override
    public boolean signatureValid(ECCParameters eccParameters, byte[] messageBytes, ECPointFp qU, BigInteger r, BigInteger s) {
        // Hash the message
        MessageDigest md = eccMessageSignerDigestFactory.create();
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(hashBytes);

        BigInteger e = ECHelper.calculateE(eccParameters, H, hashBytes);

        // Compute u1
        BigInteger u1 = e.multiply(s.modInverse(eccParameters.getN()));

        // Compute u2
        BigInteger u2 = r.multiply(s.modInverse(eccParameters.getN()));

        // Compute R = (xR, yR) = u1G + u2Qu
        ECCPoint u1G = eccParameters.getG().multiply(u1);
        ECCPoint u2Qu = qU.multiply(u2);

        ECCPoint R = u1G.add(u2Qu);

        // 5.4, if R != 0, OK
        // XXX - 0 == infinity?
        if (R.isInfinity()) {
            // Not OK
            return false;
        }

        // XXX - Convert x_R to an integer using the conversion routine in Section 2.3.9 of SEC 1
        // XXX - In Fp no conversion is necessary!

        // v = xR mod n
        BigInteger v = R.getX().toBigInteger().mod(eccParameters.getN());

        // Does v == r?
        if (v.equals(r)) {
            // Success
            return true;
        } else {
            // Failure
            return false;
        }
    }
}
