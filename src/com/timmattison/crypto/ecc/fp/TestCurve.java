package com.timmattison.crypto.ecc.fp;

import com.timmattison.crypto.ecc.*;

import javax.inject.Inject;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class TestCurve {
    private ECCCurveFactory eccCurveFactory;
    private ECCParametersFactory eccParametersFactory;
    private ECCFieldElementFactory eccFieldElementFactory;
    private ECCPointFactory eccPointFactory;
    private ECCParameters smallCurve2;

    public TestCurve() {
    }

    @Inject
    public TestCurve(ECCCurveFactory eccCurveFactory, ECCParametersFactory eccParametersFactory, ECCFieldElementFactory eccFieldElementFactory, ECCPointFactory eccPointFactory) {
        this.eccCurveFactory = eccCurveFactory;
        this.eccParametersFactory = eccParametersFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;
        this.eccPointFactory = eccPointFactory;
    }

    public BigInteger fromHex(String s) {
        return new BigInteger(s, 16);
    }

    public ECCParameters getSmallCurve1() {
        // E : y^2 = x^3 + 2*x + 2 mod 17
        BigInteger p = new BigInteger("17");
        BigInteger a = new BigInteger("2");
        BigInteger b = new BigInteger("2");

        ECCCurve curve = eccCurveFactory.create(p, a, b);

        BigInteger basePointX = new BigInteger("0");
        BigInteger basePointY = new BigInteger("0");

        ECCFieldElement fieldElementX = eccFieldElementFactory.create(curve.getP(), basePointX);
        ECCFieldElement fieldElementY = eccFieldElementFactory.create(curve.getP(), basePointY);

        ECCPoint basePoint = eccPointFactory.create(curve, fieldElementX, fieldElementY, null);

        BigInteger n = fromHex("19");
        BigInteger h = BigInteger.ONE;

        return eccParametersFactory.create(curve, basePoint, n, h);
    }

    public ECCParameters getSmallCurve2() {
        // E : y^2 = x^3 + 1*x + 1 mod 5
        BigInteger p = new BigInteger("5");
        BigInteger a = new BigInteger("1");
        BigInteger b = new BigInteger("1");

        ECCCurve curve = eccCurveFactory.create(p, a, b);

        BigInteger basePointX = new BigInteger("0");
        BigInteger basePointY = new BigInteger("0");

        ECCFieldElement fieldElementX = eccFieldElementFactory.create(curve.getP(), basePointX);
        ECCFieldElement fieldElementY = eccFieldElementFactory.create(curve.getP(), basePointY);

        ECCPoint basePoint = eccPointFactory.create(curve, fieldElementX, fieldElementY, null);

        BigInteger n = fromHex("9");
        BigInteger h = BigInteger.ONE;

        return eccParametersFactory.create(curve, basePoint, n, h);
    }
}
