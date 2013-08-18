package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.*;
import com.timmattison.crypto.ecc.fp.TestCurve;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/10/13
 * Time: 7:23 AM
 * To change this template use File | Settings | File Templates.
 */
public class SmallCurveTests {
    Injector injector = Guice.createInjector(new ECCTestModule());

    private ECCParameters getSmallCurve1() {
        return injector.getInstance(TestCurve.class).getSmallCurve1();
    }

    private ECCParameters getSmallCurve2() {
        return injector.getInstance(TestCurve.class).getSmallCurve2();
    }

    private ECCPoint getPoint(BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = getSmallCurve1().getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement, null);
    }

    @Test
    public void testInfinity() throws Exception {
        ECCParameters smallCurve = getSmallCurve1();
        ECCPoint result = smallCurve.getG().multiply(smallCurve.getN());

        Assert.assertTrue(result.isInfinity());
    }

    @Test
    public void testDoubleFirstPoint1() {
        ECCParameters smallCurve = getSmallCurve1();
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint firstPointAgain = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint result = firstPoint.twice();

        Assert.assertTrue(result.getX().toBigInteger().equals(6));
        Assert.assertTrue(result.getY().toBigInteger().equals(3));
    }

    @Test
    public void testDoubleSecondPoint1() {
        ECCParameters smallCurve = getSmallCurve1();
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint secondPoint = firstPoint.twice();
        ECCPoint result = secondPoint.twice();

        Assert.assertTrue(result.getX().toBigInteger().equals(10));
        Assert.assertTrue(result.getY().toBigInteger().equals(6));
    }
}

