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
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

    @Test
    public void testInfinity() throws Exception {
        ECCParameters smallCurve = getSmallCurve1();
        ECCPoint result = smallCurve.getG().multiply(smallCurve.getN());

        Assert.assertTrue(result.isInfinity());
    }

    @Test
    public void testDoubleFirstPoint() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint result = firstPoint.twice();

        Assert.assertTrue(result.getX().toBigInteger().equals(new BigInteger("6")));
        Assert.assertTrue(result.getY().toBigInteger().equals(new BigInteger("3")));
    }

    @Test
    public void testCalculateThirdPoint() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint secondPoint = firstPoint.twice();
        ECCPoint thirdPoint = secondPoint.add(firstPoint);

        Assert.assertTrue(thirdPoint.getX().toBigInteger().equals(new BigInteger("10")));
        Assert.assertTrue(thirdPoint.getY().toBigInteger().equals(new BigInteger("6")));
    }

    @Test
    public void testCalculateFourthPoint() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint secondPoint = firstPoint.twice();
        ECCPoint thirdPoint = secondPoint.add(firstPoint);
        ECCPoint fourthPoint = thirdPoint.add(firstPoint);

        Assert.assertTrue(fourthPoint.getX().toBigInteger().equals(new BigInteger("3")));
        Assert.assertTrue(fourthPoint.getY().toBigInteger().equals(new BigInteger("1")));
    }
}

