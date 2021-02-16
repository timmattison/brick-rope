package com.timmattison.ecc;

import com.timmattison.crypto.ecc.interfaces.*;

import javax.inject.Inject;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class CurveParametersTest {
    private ECCCurveFactory eccCurveFactory;
    private ECCParametersFactory eccParametersFactory;
    private ECCFieldElementFactory eccFieldElementFactory;
    private ECCPointFactory eccPointFactory;

    public CurveParametersTest() {
    }

    @Inject
    public CurveParametersTest(ECCCurveFactory eccCurveFactory, ECCParametersFactory eccParametersFactory, ECCFieldElementFactory eccFieldElementFactory, ECCPointFactory eccPointFactory) {
        this.eccCurveFactory = eccCurveFactory;
        this.eccParametersFactory = eccParametersFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;
        this.eccPointFactory = eccPointFactory;
    }

    public BigInteger fromHex(String s) {
        return new BigInteger(s, 16);
    }

    public ECCParameters getSmallCurve1Parameters() {
        // E : y^2 = x^3 + 2*x + 2 mod 17
        BigInteger p = new BigInteger("17");
        BigInteger a = new BigInteger("2");
        BigInteger b = new BigInteger("2");

        // n is the order of the curve
        BigInteger n = fromHex("19");

        ECCCurve curve = eccCurveFactory.create(p, n, a, b);

        BigInteger basePointX = new BigInteger("0");
        BigInteger basePointY = new BigInteger("0");

        ECCFieldElement fieldElementX = eccFieldElementFactory.create(curve.getP(), basePointX);
        ECCFieldElement fieldElementY = eccFieldElementFactory.create(curve.getP(), basePointY);

        ECCPoint basePoint = eccPointFactory.create(curve, fieldElementX, fieldElementY);

        BigInteger h = BigInteger.ONE;

        return eccParametersFactory.create(curve, basePoint, n, h);
    }

    public ECCParameters getSmallCurve2Parameters() {
        // E : y^2 = x^3 + 1*x + 1 mod 5
        BigInteger p = new BigInteger("5");
        BigInteger a = new BigInteger("1");
        BigInteger b = new BigInteger("1");

        ECCCurve curve = eccCurveFactory.create(p, p, a, b);

        BigInteger basePointX = new BigInteger("0");
        BigInteger basePointY = new BigInteger("0");

        ECCFieldElement fieldElementX = eccFieldElementFactory.create(curve.getP(), basePointX);
        ECCFieldElement fieldElementY = eccFieldElementFactory.create(curve.getP(), basePointY);

        ECCPoint basePoint = eccPointFactory.create(curve, fieldElementX, fieldElementY);

        BigInteger n = fromHex("9");
        BigInteger h = BigInteger.ONE;

        return eccParametersFactory.create(curve, basePoint, n, h);
    }
}
