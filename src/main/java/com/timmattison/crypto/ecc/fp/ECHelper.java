package com.timmattison.crypto.ecc.fp;

import com.timmattison.crypto.ecc.interfaces.ECCKeyPair;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;

import java.math.BigInteger;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

/**
 * Created by timmattison on 1/10/14.
 */
public class ECHelper {
    /**
     * Implements section 4.1.3.5 of SEC 1
     *
     * @param n
     * @param hashHexString
     * @param hashBytes
     * @return
     */
    private static BigInteger calculateE(BigInteger n, String hashHexString, byte[] hashBytes) {
        // If the ceiling of log_2 n >= (hashlen * 8) then e = H
        // Otherwise set e = leftmost log_2 n bits of H

        int logBase2OfN = n.bitLength();

        BigInteger e;

        // Can we use the whole value?
        if (logBase2OfN >= (hashBytes.length * 8)) {
            // Yes, use the whole value
            e = new BigInteger(hashHexString, 16);
        } else {
            // No, only use the leftmost log_2 n bits of H
            e = new BigInteger(hashHexString, 16);
            e = e.shiftRight(logBase2OfN - e.bitLength());
        }

        return e;
    }

    public static BigInteger calculateS(ECCKeyPair keyPair, BigInteger k, BigInteger e, BigInteger r) {
        return calculateS(k, keyPair.getN(), e, keyPair.getD(), r);
    }

    private static BigInteger calculateS(BigInteger k, BigInteger n, BigInteger e, BigInteger dU, BigInteger r) {
        return k.modPow(BigInteger.ONE.negate(), n).multiply(e.add(dU.multiply(r))).mod(n);
    }

    public static BigInteger calculateE(ECCParameters eccParameters, String hashHexString, byte[] hashBytes) {
        return calculateE(eccParameters.getN(), hashHexString, hashBytes);
    }

    public static BigInteger calculateE(ECCKeyPair keyPair, String hashHexString, byte[] hashBytes) {
        return calculateE(keyPair.getN(), hashHexString, hashBytes);
    }
}
