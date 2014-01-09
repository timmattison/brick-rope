package com.timmattison.bitcoin.old.ecc.fp;

import com.timmattison.bitcoin.old.ByteArrayHelper;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECTest2Fp {

    // (Chosen) Instantiate the dU value
    private static final BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162", 10);

    // The message
    private static final String message = "abc";
    private static final byte[] messageBytes = message.getBytes();

    public static void main(String[] args) throws Exception {
        ECKeyPairFp keyPair = new ECKeyPairFp(SECNamedCurves.getSecp160k1(), dU);

        ECSignatureFp signature = signMessage(keyPair, messageBytes);

        boolean valid = validateSignature(messageBytes, signature);

        if (!valid) {
            throw new Exception("FAILED!");
        }
    }

    public static ECSignatureFp signMessage(ECKeyPairFp keyPair, byte[] messageBytes) throws Exception {
        // Select a k value
        BigInteger k = new BigInteger("702232148019446860144825009548118511996283736794", 10);

        // Get the mod n value of k
        k = k.mod(keyPair.getN());

        // Compute R = (xR, yR) = k * G
        ECPointFp R = keyPair.getG().multiply(k);

        // Derive an integer r from xR (mod n)
        BigInteger r = R.getX().toBigInteger().mod(keyPair.getN());

        // Is r zero?
        if (r.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new Exception("r cannot be zero");
        }

        // Hash the message with SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(md.digest());

        // Calculate e
        BigInteger e = ECHelperFp.calculateE(keyPair, H, hashBytes);

        // Compute s -  s = k^-1(e + dU * r) (mod n)
        BigInteger s = ECHelperFp.calculateS(keyPair, k, e, r);

        // Is it zero (mod n)?
        if (s.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new Exception("s cannot be zero (mod n)");
        }

        ECSignatureFp signature = new ECSignatureFp(keyPair.getX9ECParameters(), r, s, keyPair.getQ());

        return signature;
    }

    private static boolean validateSignature(byte[] messageBytes, ECSignatureFp signature) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(hashBytes);

        // Calculate e
        BigInteger e = ECHelperFp.calculateE(signature.getX9ECParameters(), H, hashBytes);

        // Compute u1
        BigInteger u1 = e.multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute u2
        BigInteger u2 = signature.getR().multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute R = (xR, yR) = u1G + u2Qu
        ECPointFp u1G = signature.getG().multiply(u1);
        ECPointFp u2Qu = signature.getQu().multiply(u2);

        ECPointFp R = u1G.add(u2Qu);

        // v = xR mod n
        BigInteger v = R.getX().toBigInteger().mod(signature.getN());

        // Validate that v == r, are they equal?
        if (!v.equals(R.getX().toBigInteger())) {
            // No, return failure
            return false;
        }

        // The message is valid, return success
        return true;
    }
}

