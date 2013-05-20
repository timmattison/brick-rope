package com.timmattison.bitcoin.test.ecc;

import com.timmattison.bitcoin.test.BigIntegerHelper;
import org.bouncycastle.crypto.params.ECDomainParameters;

import java.math.BigInteger;

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
        // Instantiate big integer value
        BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162", 10);

        // Convert to octet string
        String dUOctetString = dU.toString(16);

        // Does it match our expectation?
        String expectedOctetString = "AA374FFC3CE144E6B073307972CB6D57B2A4E982";

        if (!BigIntegerHelper.compare(dUOctetString, expectedOctetString)) {
            // No, throw an exception
            throw new Exception("Failed at 1.2, expected " + expectedOctetString + ", got " + dUOctetString);
        }
    }
}
