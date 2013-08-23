package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.crypto.ecc.*;
import com.timmattison.crypto.ecc.interfaces.*;
import org.junit.Test;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECSigningTest {
    // (Chosen) Instantiate the dU value
    private final BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162", 10);
    // The message
    private final String message = "abc";
    private final byte[] messageBytes = message.getBytes();

    @Test
    public void test2() throws Exception {
        ECCKeyPair keyPair = createECCKeyPair(getSecp160k1(), dU);

        ECCSignature signature = signMessage(keyPair, messageBytes);

        boolean valid = validateSignature(messageBytes, signature);

        if (!valid) {
            throw new Exception("FAILED!");
        }
    }

    private ECCParameters getSecp160k1() {
        Injector injector = Guice.createInjector(new ECCTestModule());
        return injector.getInstance(ECCNamedCurve.class).getSecp160k1();
    }

    private ECCKeyPair createECCKeyPair(ECCParameters eccParameters, BigInteger dU) {
        Injector injector = Guice.createInjector(new ECCTestModule());
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    private ECCSignature createECCSignature(ECCParameters eccParameters, BigInteger r, BigInteger dU, ECCPoint q) {
        Injector injector = Guice.createInjector(new ECCTestModule());
        return injector.getInstance(ECCSignatureFactory.class).create(eccParameters, r, dU, q);
    }

    public ECCSignature signMessage(ECCKeyPair keyPair, byte[] messageBytes) throws Exception {
        // Select a k value
        BigInteger k = new BigInteger("702232148019446860144825009548118511996283736794", 10);

        // Get the mod n value of k
        k = k.mod(keyPair.getN());

        // Compute R = (xR, yR) = k * G
        ECCPoint R = keyPair.getG().multiply(k);

        // Derive an integer r from xR (mod n)
        BigInteger r = R.getX().toBigInteger().mod(keyPair.getN());

        // Is r zero?
        if (r.equals(BigInteger.ZERO)) {
            // No, throw an exception
            throw new Exception("r cannot be zero");
        }

        // Hash the message with SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(md.digest());

        // Calculate e
        BigInteger e = ECHelper.calculateE(keyPair, H, hashBytes);

        // Compute s -  s = k^-1(e + dU * r) (mod n)
        BigInteger s = ECHelper.calculateS(keyPair, k, e, r);

        // Is it zero (mod n)?
        if (s.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new Exception("s cannot be zero (mod n)");
        }

        ECCSignature signature = createECCSignature(keyPair.getECCParameters(), r, s, keyPair.getQ());

        return signature;
    }

    private boolean validateSignature(byte[] messageBytes, ECCSignature signature) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(hashBytes);

        // Calculate e
        BigInteger e = ECHelper.calculateE(signature.getECCParameters(), H, hashBytes);

        // Compute u1
        BigInteger u1 = e.multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute u2
        BigInteger u2 = signature.getR().multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute R = (xR, yR) = u1G + u2Qu
        ECCPoint u1G = signature.getG().multiply(u1);
        ECCPoint u2Qu = signature.getQu().multiply(u2);

        ECCPoint R = u1G.add(u2Qu);

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

