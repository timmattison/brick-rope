package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.factories.ECCParamsFactory;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/15/13
 * Time: 7:14 AM
 * To change this template use File | Settings | File Templates.
 */

public class BitcoinSignatureProcessor implements SignatureProcessor<ECCSignature> {
    private final ECCParamsFactory eccParamsFactory;
    private final ECCSignatureFactory signatureFactory;

    @Inject
    public BitcoinSignatureProcessor(ECCParamsFactory eccParamsFactory, ECCSignatureFactory signatureFactory) {
        this.eccParamsFactory = eccParamsFactory;
        this.signatureFactory = signatureFactory;
    }

    @Override
    public ECCSignature getSignature(byte[] signature, byte[] publicKey) {
        //System.out.println("Public key bytes: " + ByteArrayHelper.toHex(publicKey));
        //System.out.println("Signature bytes: " + ByteArrayHelper.toHex(signature));

        // Sanity check: Signature starts with 0x30
        if (signature[0] != 0x30) {
            throw new UnsupportedOperationException("Signature does not start with 0x30");
        }

        // Sanity check: r starts with 0x02
        if (signature[2] != 0x02) {
            throw new UnsupportedOperationException("r does not start with 0x02");
        }

        // TODO Sanity check: rLength makes sense
        int rLength = signature[3];

        // Extract r
        int rStart = 3 + 1;
        int rEnd = rStart + rLength;
        byte[] r = Arrays.copyOfRange(signature, rStart, rEnd);

        // TODO Sanity check: sLength makes sense
        int sLength = signature[rEnd + 1];

        // Extract s
        int sStart = rEnd + 2;
        int sEnd = sStart + sLength;
        byte[] s = Arrays.copyOfRange(signature, sStart, sEnd);

        //System.out.println("R bytes: " + ByteArrayHelper.toHex(r));
        //System.out.println("S bytes: " + ByteArrayHelper.toHex(s));

        // Sanity check rsLength makes sense
        int rsLength = signature[1];

        int expectedRsLength = rLength + sLength + 4;

        if (expectedRsLength != rsLength) {
            throw new UnsupportedOperationException("rsLength is incorrect [expected " + expectedRsLength + ", actual " + rsLength + "]");
        }

        // Convert R, S, and the public key into an ECC signature object
        return getSignature(r, s, publicKey);
    }

    @Override
    public ECCSignature getSignature(byte[] sig_r, byte[] sig_s, byte[] publicKey) {
        // Get the curve from the ECC parameters
        ECCParameters ecc = eccParamsFactory.create();
        ECCCurve curve = ecc.getCurve();

        // Decode the point from the binary public key
        ECCPoint Qu = curve.decodePointBinary(publicKey);

        // Add the proper padding to sig_r and sig_s
        sig_r = addProperPadding(sig_r);
        sig_s = addProperPadding(sig_s);

        // Convert sig_r and sig_s to BigInteger objects
        BigInteger signatureR = new BigInteger(sig_r);
        BigInteger signatureS = new BigInteger(sig_s);

        // Create the signature instance
        ECCSignature eccSignature = signatureFactory.create(ecc, signatureR, signatureS, Qu);

        return eccSignature;
    }

    private byte[] addProperPadding(byte[] input) {
        byte[] output = input;

        if ((input[0] & 0x80) == 0x80) {
            output = new byte[input.length + 1];
            output[0] = 0;

            // TODO: Use System.arraycopy here
            for (int loop = 0; loop < input.length; loop++) {
                output[loop + 1] = input[loop];
            }
        }

        return output;
    }
}
