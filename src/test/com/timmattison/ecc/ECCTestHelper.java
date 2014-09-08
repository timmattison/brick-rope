package com.timmattison.ecc;

import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.ecc.random.BigIntegerRandomForTesting;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/21/13
 * Time: 6:54 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECCTestHelper {
    public static ECCPoint getPoint(Injector injector, ECCParameters parameters, BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = parameters.getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

    public static ECCParameters getSmallCurve1Parameters(Injector injector) {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve1Parameters();
    }

    public static ECCParameters getSmallCurve2Parameters(Injector injector) {
        return injector.getInstance(TestCurveParameters.class).getSmallCurve2Parameters();
    }

    public static ECCParameters getSecp160r1(Injector injector) {
        return injector.getInstance(ECCNamedCurveFp.class).getSecp160r1();
    }

    public static ECCParameters getSecp256k1(Injector injector) {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp256k1();
    }

    public static ECCParameters getSecp256r1(Injector injector) {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp256r1();
    }

    public static ECCKeyPair getKeyPair(Injector injector, ECCParameters eccParameters, BigInteger dU) {
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    public static ECCMessageSignatureVerifier getSignatureVerifier(Injector injector) {
        return injector.getInstance(ECCMessageSignatureVerifierFactory.class).create();
    }

    public static ECCSignature getSignature(Injector injector, ECCParameters eccParameters, BigInteger r, BigInteger s, ECCPoint Qu) {
        return injector.getInstance(ECCSignatureFactory.class).create(eccParameters, r, s, Qu);
    }

    public static Object getSignature(Injector injector, byte[] r, byte[] s, byte[] publicKey) {
        return injector.getInstance(SignatureProcessorFactory.class).create().getSignature(r, s, publicKey);
    }

    public static ECCMessageSigner getSigner(Injector injector, BigInteger valueToReturn, ECCKeyPair eccKeyPair) {
        BigIntegerRandomForTesting bigIntegerRandom = injector.getInstance(BigIntegerRandomForTesting.class);
        bigIntegerRandom.setValueToReturn(valueToReturn);
        return injector.getInstance(ECCMessageSignerFactory.class).create(bigIntegerRandom, eccKeyPair);
    }

    public static ECCPoint getBasePoint(Injector injector) {
        return ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), new BigInteger("5"), new BigInteger("1"));
    }

    public static String toBitStringFromHexString(String input) {
        if ((input.length() % 2) != 0) {
            throw new UnsupportedOperationException("This doesn't appear to be a properly padding hex string.  The length (mod 2) isn't zero.");
        }

        BigInteger temp = new BigInteger(input, 16);

        StringBuilder stringBuilder = new StringBuilder();

        // Loop through the bits
        for (int loop = 0; loop < temp.bitLength(); loop++) {
            stringBuilder.append(temp.testBit(temp.bitLength() - loop - 1) ? "1" : "0");
        }

        return stringBuilder.toString();
    }

    public static String toHexStringFromBitString(String input) {
        if ((input.length() % 8) != 0) {
            throw new UnsupportedOperationException("This doesn't appear to be a properly padded binary string.  The length (mod 8) isn't zero.");
        }

        BigInteger temp = new BigInteger(input, 2);

        return temp.toString(16);
    }

    public static boolean compare(String first, String second) {
        // Convert to lowercase and remove all spaces
        first = first.toLowerCase().replaceAll(" ", "");
        second = second.toLowerCase().replaceAll(" ", "");

        return first.equals(second);
    }
}
