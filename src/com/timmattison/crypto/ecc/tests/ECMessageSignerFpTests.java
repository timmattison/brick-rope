package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignerFpTests {
    Injector injector = Guice.createInjector(new ECCTestModule());
    ECCCurveFactory eccCurveFactory = injector.getInstance(ECCCurveFactory.class);
    ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
    ECCParametersFactory eccParametersFactory = injector.getInstance(ECCParametersFactory.class);

    private ECCParameters getSecp256k1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp256k1();
    }

    private ECCParameters getSecp160r1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp160r1();
    }

    private ECCPoint getPoint(ECCParameters eccParameters, BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = eccParameters.getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

    private ECCKeyPair getKeyPair(ECCParameters eccParameters, BigInteger dU) {
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    private ECCMessageSigner getSigner(ECCKeyPair eccKeyPair, Random random) {
        return injector.getInstance(ECCMessageSignerFactory.class).create(eccKeyPair, random);
    }

    /**
     * GEC2 2.1
     */
    @Test
    public void testGec2_2_1() {
        BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162");
        ECCKeyPair eccKeyPair = getKeyPair(getSecp256k1(), dU);

        // XXX - How do I test a specific k here?  Maybe need to make a BigIntegerRandom interface first.
        ECCMessageSigner signer = getSigner(eccKeyPair, new Random());
                ECCSignature test = signer.signMessage("abc".getBytes());
    }
}
