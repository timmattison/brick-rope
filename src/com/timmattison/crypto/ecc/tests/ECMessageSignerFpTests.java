package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.crypto.ecc.random.impl.BigIntegerRandomForTesting;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignerFpTests {
    static Injector injector = Guice.createInjector(new ECCTestModule());
    ECCCurveFactory eccCurveFactory = injector.getInstance(ECCCurveFactory.class);
    ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
    ECCParametersFactory eccParametersFactory = injector.getInstance(ECCParametersFactory.class);

    private static ECCParameters getSecp160r1() {
        return injector.getInstance(ECCNamedCurveFactory.class).create().getSecp160r1();
    }

    private ECCPoint getPoint(ECCParameters eccParameters, BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = eccParameters.getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

    private static ECCKeyPair getKeyPair(ECCParameters eccParameters, BigInteger dU) {
        return injector.getInstance(ECCKeyPairFactory.class).create(eccParameters, dU);
    }

    private ECCMessageSignatureVerifier getSignatureVerifier() {
        return injector.getInstance(ECCMessageSignatureVerifierFactory.class).create();
    }

    private ECCMessageSigner getSigner(BigInteger valueToReturn, ECCKeyPair eccKeyPair) {
        BigIntegerRandomForTesting bigIntegerRandom = injector.getInstance(BigIntegerRandomForTesting.class);
        bigIntegerRandom.setValueToReturn(valueToReturn);
        return injector.getInstance(ECCMessageSignerFactory.class).create(bigIntegerRandom, eccKeyPair);
    }

    private static final BigInteger gec2_1_3_r = new BigInteger("1176954224688105769566774212902092897866168635793");
    private static final BigInteger gec2_1_3_s = new BigInteger("299742580584132926933316745664091704165278518100");
    private static final BigInteger gec2_1_3_chosenK = new BigInteger("702232148019446860144825009548118511996283736794");
    private static final BigInteger gec2_1_3_dU = new BigInteger("971761939728640320549601132085879836204587084162");
    private static final ECCKeyPair eccKeyPair = getKeyPair(getSecp160r1(), gec2_1_3_dU);
    private static final ECCPoint gec2_1_4_Qu = eccKeyPair.getQ();
    private static final byte[] gec2_1_3_messageBytes = "abc".getBytes();

    /**
     * GEC2 2.1.3 - Signing operation for U
     */
    @Test
    public void testGec2_1_3() {

        // Specific k value set
        ECCMessageSigner signer = getSigner(gec2_1_3_chosenK, eccKeyPair);
        ECCSignature test = signer.signMessage(gec2_1_3_messageBytes);

        // Validate the signature
        Assert.assertEquals(gec2_1_3_r, test.getR());
        Assert.assertEquals(gec2_1_3_s, test.getS());
    }

    /**
     * GEC2 2.1.4 - Verifying operation for V
     */
    @Test
    public void testGec2_1_4() {
        BigInteger r = gec2_1_3_r;
        BigInteger s = gec2_1_3_r;

        BigInteger n = getSecp160r1().getN();

        // r and s must be integers in the interval [1, n - 1]
        BigInteger two = BigInteger.valueOf(2);

        // r and s must be >= 2
        Assert.assertTrue(r.compareTo(two) >= 0);
        Assert.assertTrue(s.compareTo(two) >= 0);

        // r and s must be < n
        Assert.assertTrue(r.compareTo(n) < 0);
        Assert.assertTrue(s.compareTo(n) < 0);

        ECCMessageSignatureVerifier signatureVerifier = getSignatureVerifier();

        Assert.assertTrue(signatureVerifier.signatureValid(getSecp160r1(), gec2_1_3_messageBytes, gec2_1_4_Qu, gec2_1_3_r, gec2_1_3_s));
    }
}
