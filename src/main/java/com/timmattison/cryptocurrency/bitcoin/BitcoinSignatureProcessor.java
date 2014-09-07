package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.factories.ECCParamsFactory;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

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

    public BitcoinSignatureProcessor(ECCParamsFactory eccParamsFactory, ECCSignatureFactory signatureFactory) {
        this.eccParamsFactory = eccParamsFactory;
        this.signatureFactory = signatureFactory;
    }

    @Override
    public ECCSignature getSignature(byte[] signature, byte[] publicKey) {
        /*
        Signature should be in this format (from http://www.bitcoinsecurity.org/2012/07/22/7/):
          [sig] = [sigLength][0×30][rsLength][0×02][rLength][sig_r][0×02][sLength][sig_s][0×01]
            where
              sigLength gives the number of bytes taken up the rest of the signature ([0×30]…[0×01])
              rsLength gives the number of bytes in [0×02][rLength][sig_r][0×02][sLength][sig_s]
              rLength gives the number of bytes in [sig_r] (approx 32 bytes)
              sLength gives the number of bytes in [sig_s] (approx 32 bytes)
         */

        // At this point we don't have the sigLength bytes so let's validate what we know for sure

        // First byte must be 0x30
        if (signature[0] != 0x30) {
            throw new UnsupportedOperationException("Signature does not start with 0x30");
        }

        // Second byte must be the length of the data minus 3 (1 for the byte we just checked, 1 for this byte,
        //   and 1 for the last byte which is the signature hash type)
        if (signature[1] != (signature.length - 3)) {
            throw new UnsupportedOperationException("Invalid rsLength [" + signature[1] + ", " + signature.length + "]");
        }

        // Third byte must be 0x02
        if (signature[2] != 0x02) {
            throw new UnsupportedOperationException("sig_r Signature type is not 0x02 [" + signature[2] + "]");
        }

        // sig_r length must be (XXX - docs say approx 32 bytes - XXX) 32 bytes
        if (signature[3] != 32) {
            throw new UnsupportedOperationException("rLength is not 32 [" + signature[3] + "]");
        }

        // First byte after sig_r must be 0x02
        if (signature[4 + 32] != 0x02) {
            throw new UnsupportedOperationException("sig_s Signature type is not 0x02 [" + signature[3 + 32] + "]");
        }

        // sig_s length must be (XXX - docs say approx 32 bytes - XXX) 32 bytes
        if (signature[4 + 32 + 1] != 32) {
            throw new UnsupportedOperationException("sLength is not 32 [" + signature[3 + 32 + 1] + "]");
        }

        // Last byte must be a valid signature hash value
        byte hashTypeByte = signature[signature.length - 1];

        if (BitcoinHashType.convert(hashTypeByte) == null) {
            throw new UnsupportedOperationException("Unsupported hash type [" + hashTypeByte + "]");
        }

        // Everything looks good.  Extract sig_r and sig_s.
        byte[] sig_r = Arrays.copyOfRange(signature, 4, 4 + 32);
        byte[] sig_s = Arrays.copyOfRange(signature, 4 + 32 + 2, 4 + 32 + 2 + 32);

        return getSignature(sig_r, sig_s, publicKey);
    }

    @Override
    public ECCSignature getSignature(byte[] sig_r, byte[] sig_s, byte[] publicKey) {
        // Get the curve from the ECC parameters
        ECCParameters ecc = eccParamsFactory.create();
        ECCCurve curve = ecc.getCurve();

        // Decode the point from the binary public key
        ECCPoint Qu = curve.decodePointBinary(publicKey);

        sig_r = addProperPadding(sig_r);
        sig_s = addProperPadding(sig_s);

        // Create the signature instance
        BigInteger signatureR = new BigInteger(sig_r);
        BigInteger signatureS = new BigInteger(sig_s);

        ECCSignature ecSignature = signatureFactory.create(ecc, signatureR, signatureS, Qu);
        //ECCSignature ecSignature = signatureFactory.create(ecc, new BigInteger(ByteArrayHelper.reverseBytes(sig_r)), new BigInteger(ByteArrayHelper.reverseBytes(sig_s)), Qu);

        return ecSignature;
    }

    private byte[] addProperPadding(byte[] input) {
        byte[] output = input;

        if ((input[0] & 0x80) == 0x80) {
            output = new byte[input.length + 1];
            output[0] = 0;

            for (int loop = 0; loop < input.length; loop++) {
                output[loop + 1] = input[loop];
            }
        }

        return output;
    }
}
