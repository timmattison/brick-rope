package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.ECHelper;
import com.timmattison.crypto.ecc.tests.modules.ECCBitcoinTestModule;
import com.timmattison.crypto.ecc.tests.modules.ECCSECTestModule;
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
public class ECKeyDeploymentAndSigningTest {
    Injector injector = Guice.createInjector(new ECCSECTestModule());

    // (Chosen) Instantiate the dU value
    private final BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162", 10);

    // Calculated in the first method
    private ECCPoint Qu;

    // The message
    private final String message = "abc";
    private final byte[] messageBytes = message.getBytes();

    // Derived from xR
    private ECCPoint R;

    // Message signature
    private BigInteger s;

    // Derive an integer r from xR (mod n)
    private BigInteger r;

    /**
     * From GEC 2: Test Vectors for SEC 1, 2.1.2
     */
    @Test
    public void test1() throws Exception {
        step1KeyDeploymentForU();

        step2SigningOperationForU();

        step3ValidateSignatureForV();
    }

   private void step1KeyDeploymentForU() throws Exception {
        ECCParameters secp160r1 = ECCTestHelper.getSecp160r1(injector);

        // Convert to octet string
        String dUOctetString = dU.toString(16);

        // Does it match our expectation?
        String expectedOctetString = "AA374FFC3CE144E6B073307972CB6D57B2A4E982";

        if (!ECHelper.compare(dUOctetString, expectedOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.2 1.2, expected " + expectedOctetString + ", got " + dUOctetString);
        }

        // Calculate Qu = (xU, yU) = dU * G
        Qu = secp160r1.getG().multiply(dU);

        // Validate Qu
        BigInteger xU = new BigInteger("466448783855397898016055842232266600516272889280", 10);
        BigInteger yU = new BigInteger("1110706324081757720403272427311003102474457754220", 10);

        // Does xU match?
        BigInteger QuX = Qu.getX().toBigInteger();

        if (!QuX.equals(xU)) {
            // No, throw an exception
            throw new Exception("Failed at 2, Qu doesn't match xU.  Expected " + xU.toString(16) + ", got " + QuX.toString(16));
        }

        // Does yU match?
        BigInteger QuY = Qu.getY().toBigInteger();

        if (!QuY.equals(yU)) {
            // No, throw an exception
            throw new Exception("Failed at 2, Qu doesn't match yU.  Expected " + yU.toString(16) + ", got " + QuY.toString(16));
        }

        // TODO - Validate the Qu octet string
        // String expectedQuOctetString = "0251b4496fecc406ed0e75a24a3c03206251419dc0";
    }

    private void step2SigningOperationForU() throws Exception {
        ECCParameters secp160r1 = ECCTestHelper.getSecp160r1(injector);

        // Selected k value
        BigInteger k = new BigInteger("702232148019446860144825009548118511996283736794", 10);

        // Compute R = (xR, yR) = k * G
        R = secp160r1.getG().multiply(k);

        // Validate R
        BigInteger xR = new BigInteger("1176954224688105769566774212902092897866168635793", 10);
        BigInteger yR = new BigInteger("1130322298812061698910820170565981471918861336822", 10);

        // Does xR match?
        BigInteger RX = R.getX().toBigInteger();

        if (!RX.equals(xR)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 1.2, R doesn't match xR.  Expected " + xR.toString(16) + ", got " + RX.toString(16));
        }

        // Does yR match?
        BigInteger RY = R.getY().toBigInteger();

        if (!RY.equals(yR)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 1.2, R doesn't match yR.  Expected " + yR.toString(16) + ", got " + RY.toString(16));
        }

        // Derive an integer r from xR (mod n)
        r = new BigInteger("1176954224688105769566774212902092897866168635793", 10).mod(secp160r1.getN());

        // Is r zero?
        if (r.equals(BigInteger.ZERO)) {
            // Yes, r cannot be zero
            throw new Exception("Failed at 2.1.3 3.2.  r cannot be zero.");
        }

        // Validate r as an octet string
        String rOctetString = "ce2873e5be449563391feb47ddcba2dc16379191";
        String rString = r.toString(16);

        // Is it correct?
        if (!ECHelper.compare(rString, rOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 3.3.  Expected " + rOctetString + ", got " + rString);
        }

        // Hash the message with SHA-1
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        String H = ByteArrayHelper.toHex(md.digest());

        // Validate the message hash
        String expectedMessageHash = "a9993e364706816aba3e25717850c26c9cd0d89d";

        // Is the hash what we expected?
        if (!ECHelper.compare(H, expectedMessageHash)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 4.  Expected " + expectedMessageHash + ", got " + H);
        }

        // Derive e from H

        // Convert H to a bit string
        String bitStringH = ECHelper.toBitStringFromHexString(H);

        // Validate the bit string
        String expectedBitString = "10101001 10011001 00111110 00110110 01000111 00000110 10000001 01101010 10111010 00111110 00100101 01110001 01111000 01010000 11000010 01101100 10011100 11010000 11011000 10011101";

        // Are they equal?
        if (!ECHelper.compare(bitStringH, expectedBitString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 5.1.  Expected " + expectedBitString + ", got " + bitStringH);
        }

        int lengthCheckValue = bitStringH.length() % 8;

        // TODO - Validate that this is what we should be checking.  The notation is a bit unclear to me in the docs.
        if (lengthCheckValue != 0) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 5.2.  Length of bit string for H must be 0 (mod 8).");
        }

        // Set E string to H string since length H mod 8 equals 0
        String bitStringE = new String(bitStringH);

        // Convert from the bit string to a hex string
        String hexStringE = ECHelper.toHexStringFromBitString(bitStringE);

        // Validate the hex string
        String expectedHexString = "A9993E364706816ABA3E25717850C26C9CD0D89D";

        // Are they equal?
        if (!ECHelper.compare(hexStringE, expectedHexString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 5.3.  Expected " + expectedHexString + ", got " + hexStringE);
        }

        BigInteger E = new BigInteger(hexStringE, 16);

        // Validate that E is the correct value
        BigInteger expectedE = new BigInteger("968236873715988614170569073515315707566766479517", 10);

        // Are they equal?
        if (!expectedE.equals(E)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 5.4.  Expected " + expectedE + ", got " + E);
        }

        // Step 6: Compute the integer s.

        // s = k^-1(e + dU * r) (mod n)
        s = k.modPow(BigInteger.ONE.negate(), secp160r1.getN()).multiply(E.add(dU.multiply(r))).mod(secp160r1.getN());

        // Validate that s is not zero (mod n)

        // Is it zero (mod n)?
        if (s.equals(BigInteger.ZERO)) {
            // Yes, throw an exception
            throw new Exception("s cannot be zero (mod n)");
        }

        // Validate that s is the value we expect
        BigInteger expectedS = new BigInteger("299742580584132926933316745664091704165278518100", 10);

        // Is it equal to the value we expect?
        if (!s.equals(expectedS)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 6.1.  Expected " + expectedS + ", got " + s);
        }

        // Validate the octet string representation of the signature
        String expectedSOctetString = "3480EC371A091A464B31CE47DF0CB8AA2D98B54";

        // Are they equal?
        if (ECHelper.compare(s.toString(16), expectedSOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.3 6.3.  Expected " + expectedSOctetString + ", got " + s);
        }
    }

    private void step3ValidateSignatureForV() throws Exception {
        ECCParameters secp160r1 = ECCTestHelper.getSecp160r1(injector);

        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        String H = ByteArrayHelper.toHex(md.digest());

        // Convert H to a bit string
        String bitStringE = ECHelper.toBitStringFromHexString(H);

        // Convert the bit string to a hex string
        String hexStringE = ECHelper.toHexStringFromBitString(bitStringE);

        BigInteger e = new BigInteger(hexStringE, 16);

        // Compute u1
        BigInteger u1 = e.multiply(s.modPow(BigInteger.ONE.negate(), secp160r1.getN())).mod(secp160r1.getN());

        // Does u1 match our expectation?
        BigInteger expectedU1 = new BigInteger("126492345237556041805390442445971246551226394866", 10);

        if (!u1.equals(expectedU1)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 4.  u1 didn't match.");
        }

        // Compute u2
        BigInteger u2 = r.multiply(s.modPow(BigInteger.ONE.negate(), secp160r1.getN())).mod(secp160r1.getN());

        // Does u2 match our expectation?
        BigInteger expectedU2 = new BigInteger("642136937233451268764953375477669732399252982122", 10);

        if (!u2.equals(expectedU2)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 4.  u2 didn't match.");
        }

        // Compute R = (xR, yR) = u1G + u2Qu
        ECCPoint u1G = secp160r1.getG().multiply(u1);
        ECCPoint u2Qu = Qu.multiply(u2);

        ECCPoint R = u1G.add(u2Qu);

        // Validate that R is what we expect
        BigInteger expectedXr = new BigInteger("1176954224688105769566774212902092897866168635793", 10);
        BigInteger expectedYr = new BigInteger("1130322298812061698910820170565981471918861336822", 10);

        // Are the points equal?
        if (!R.getX().toBigInteger().equals(expectedXr)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 5.3.  xR didn't match.");
        }

        if (!R.getY().toBigInteger().equals(expectedYr)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 5.3.  yR didn't match.");
        }

        // v = xR mod n
        BigInteger v = R.getX().toBigInteger().mod(secp160r1.getN());

        // Validate that v is what we expect
        BigInteger expectedV = new BigInteger("1176954224688105769566774212902092897866168635793", 10);

        if (!v.equals(expectedV)) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 7.  v didn't match");
        }

        // Validate that v == r, are they equal?
        if (!v.equals(R.getX().toBigInteger())) {
            // No, throw an exception
            throw new Exception("Failed at 2.1.4 8.  v != r");
        }

        // The message is valid
    }
}

