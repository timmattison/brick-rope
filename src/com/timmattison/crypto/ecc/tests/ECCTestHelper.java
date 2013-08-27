package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.BigIntegerRandomForTesting;

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
        return injector.getInstance(ECCNamedCurve.class).getSecp160r1();
    }

    public static ECCParameters getSecp256k1(Injector injector) {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp256k1();
    }

    public static ECCKeyPair getKeyPair(Injector injector, ECCParameters eccParameters, BigInteger dU) {
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    public static ECCMessageSignatureVerifier getSignatureVerifier(Injector injector) {
        return injector.getInstance(ECCMessageSignatureVerifierFactory.class).create();
    }

    public static ECCMessageSigner getSigner(Injector injector, BigInteger valueToReturn, ECCKeyPair eccKeyPair) {
        BigIntegerRandomForTesting bigIntegerRandom = injector.getInstance(BigIntegerRandomForTesting.class);
        bigIntegerRandom.setValueToReturn(valueToReturn);
        return injector.getInstance(ECCMessageSignerFactory.class).create(bigIntegerRandom, eccKeyPair);
    }

    public static ECCPoint getBasePoint(Injector injector) {
        return ECCTestHelper.getPoint(injector, ECCTestHelper.getSmallCurve1Parameters(injector), new BigInteger("5"), new BigInteger("1"));
    }
}
