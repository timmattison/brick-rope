package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.BigIntegerRandomForTesting;
import com.timmattison.crypto.ecc.random.impl.RealBigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import junit.framework.Assert;
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

    private ECCMessageSigner getSigner(BigInteger valueToReturn, ECCKeyPair eccKeyPair) {
        BigIntegerRandomForTesting bigIntegerRandom = injector.getInstance(BigIntegerRandomForTesting.class);
        bigIntegerRandom.setValueToReturn(valueToReturn);
        return injector.getInstance(ECCMessageSignerFactory.class).create(bigIntegerRandom, eccKeyPair);
    }

    /**
     * GEC2 2.1
     */
    @Test
    public void testGec2_2_1() {
        BigInteger chosenK = new BigInteger("702232148019446860144825009548118511996283736794");
        BigInteger dU = new BigInteger("971761939728640320549601132085879836204587084162");
        ECCKeyPair eccKeyPair = getKeyPair(getSecp160r1(), dU);

        // Specific k value set
        ECCMessageSigner signer = getSigner(chosenK, eccKeyPair);
        ECCSignature test = signer.signMessage("abc".getBytes());

        // Validate the signature
        BigInteger r = new BigInteger("1176954224688105769566774212902092897866168635793");
        BigInteger s = new BigInteger("299742580584132926933316745664091704165278518100");

        Assert.assertEquals(r, test.getR());
        Assert.assertEquals(s, test.getS());
    }
}
