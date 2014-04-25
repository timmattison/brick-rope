package com.timmattison.crypto.ecc.fp;

import com.google.inject.Inject;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Hash;

import java.math.BigInteger;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignatureVerifierFp implements ECCMessageSignatureVerifier {
    private final ECCMessageSignerHashFactory eccMessageSignerHashFactory;

    private final static BigInteger two = BigInteger.valueOf(2);

    @Inject
    public ECMessageSignatureVerifierFp(ECCMessageSignerHashFactory eccMessageSignerHashFactory) {
        this.eccMessageSignerHashFactory = eccMessageSignerHashFactory;
    }

    @Override
    public boolean signatureValid(byte[] messageBytes, ECCSignature eccSignature) {
        ECCParameters eccParameters = eccSignature.getECCParameters();
        BigInteger r = eccSignature.getR();
        BigInteger s = eccSignature.getS();
        ECCPoint Qu = eccSignature.getQu();

        // Hash the message
        Hash hash = eccMessageSignerHashFactory.create(messageBytes);
        byte[] hashBytes = hash.getOutput();
        String H = ByteArrayHelper.toHex(hashBytes);

        // r and s must be >= 2
        if (r.compareTo(two) < 0) {
            return false;
        }

        if (s.compareTo(two) < 0) {
            return false;
        }

        // r and s must be < n
        if (r.compareTo(eccParameters.getN()) >= 0) {
            return false;
        }

        if (s.compareTo(eccParameters.getN()) >= 0) {
            return false;
        }

        // Compute e
        BigInteger e = ECHelper.calculateE(eccParameters, H, hashBytes);

        // Compute u1
        BigInteger u1 = e.multiply(s.modInverse(eccParameters.getN()));

        // Compute u2
        BigInteger u2 = r.multiply(s.modInverse(eccParameters.getN()));

        // Compute R = (xR, yR) = u1G + u2Qu
        ECCPoint u1G = eccParameters.getG().multiply(u1);
        ECCPoint u2Qu = Qu.multiply(u2);

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
