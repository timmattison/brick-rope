package com.timmattison.bitcoin.test.ecc;

import com.timmattison.bitcoin.test.BigIntegerHelper;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import org.bouncycastle.crypto.params.ECDomainParameters;

import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECTest {
    public static void main(String[] args) throws Exception {
        try {
            test1();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * From GEC 2: Test Vectors for SEC 1, 2.1.2
     */
    private static void test1() throws Exception {
        ECPointFp Qu = keyDeploymentForU();

        signingOperationForU(Qu);
    }

    private static void signingOperationForU(ECPointFp qu) throws Exception {
        X9ECParameters secp160r1 = SECNamedCurves.getSecp160r1();

        // Selected k value
        BigInteger k = new BigInteger("702232148019446860144825009548118511996283736794", 10);

        // Compute R = (xR, yR) = k * G
        ECPointFp R = secp160r1.getG().multiply(k);

        // Validate R
        BigInteger xR = new BigInteger("1176954224688105769566774212902092897866168635793", 10);
        BigInteger yR = new BigInteger("1130322298812061698910820170565981471918861336822", 10);

        // Does xR match?
        BigInteger RX = R.getX().toBigInteger();

        if(!BigIntegerHelper.equals(RX, xR)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 1.2, R doesn't match xR.  Expected " + xR.toString(16) + ", got " + RX.toString(16));
        }

        // Does yR match?
        BigInteger RY = R.getY().toBigInteger();

        if(!BigIntegerHelper.equals(RY, yR)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 1.2, R doesn't match yR.  Expected " + yR.toString(16) + ", got " + RY.toString(16));
        }

        // Derive an integer r from xR (mod n)
        BigInteger r = new BigInteger("1176954224688105769566774212902092897866168635793", 10);

        // Is r zero?
        if(BigIntegerHelper.equals(r, BigInteger.ZERO)) {
            // Yes, r cannot be zero
            throw new Exception("Failed at 2.1.3 3.2.  r cannot be zero.");
        }

        // Validate r as an octet string
        String rOctetString = "ce2873e5be449563391feb47ddcba2dc16379191";
        String rString = r.toString(16);

        // Is it correct?
        if(!BigIntegerHelper.compare(rString, rOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 3.3.  Expected " + rOctetString + ", got " + rString);
        }

        // Message is "abc"
        String M = "abc";

        // Hash the message with SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(M.getBytes());
        String digestString = ByteArrayHelper.toHex(md.digest());

        // Validate the message hash
        String expectedMessageHash = "a9993e364706816aba3e25717850c26c9cd0d89d";

        // Is the hash what we expected?
        if(!BigIntegerHelper.compare(digestString, expectedMessageHash)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 4.  Expected " + expectedMessageHash + ", got " + digestString);
        }
    }

    private static ECPointFp keyDeploymentForU() throws Exception {
        X9ECParameters secp160r1 = SECNamedCurves.getSecp160r1();

        // Instantiate big integer value
        BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162", 10);

        // Convert to octet string
        String dUOctetString = dU.toString(16);

        // Does it match our expectation?
        String expectedOctetString = "AA374FFC3CE144E6B073307972CB6D57B2A4E982";

        if (!BigIntegerHelper.compare(dUOctetString, expectedOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.2 1.2, expected " + expectedOctetString + ", got " + dUOctetString);
        }

        // Calculate Qu = (xU, yU) = dU * G
        ECPointFp Qu = secp160r1.getG().multiply(dU);

        // Validate Qu
        BigInteger xU = new BigInteger("466448783855397898016055842232266600516272889280", 10);
        BigInteger yU = new BigInteger("1110706324081757720403272427311003102474457754220", 10);

        // Does xU match?
        BigInteger QuX = Qu.getX().toBigInteger();

        if(!BigIntegerHelper.equals(QuX, xU)) {
            // No, throw an exception
            throw new Exception("Failed at 2, Qu doesn't match xU.  Expected " + xU.toString(16) + ", got " + QuX.toString(16));
        }

        // Does yU match?
        BigInteger QuY = Qu.getY().toBigInteger();

        if(!BigIntegerHelper.equals(QuY, yU)) {
            // No, throw an exception
            throw new Exception("Failed at 2, Qu doesn't match yU.  Expected " + yU.toString(16) + ", got " + QuY.toString(16));
        }

        // TODO - Validate the Qu octet string
        // String expectedQuOctetString = "0251b4496fecc406ed0e75a24a3c03206251419dc0";

        return Qu;
    }
}
