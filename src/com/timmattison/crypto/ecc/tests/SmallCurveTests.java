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

    private void validatePoint(ECCPoint point, int x, int y) {
        Assert.assertTrue(point.getX().toBigInteger().equals(BigInteger.valueOf(x)));
        Assert.assertTrue(point.getY().toBigInteger().equals(BigInteger.valueOf(y)));
    }

    @Test
    public void testCalculateFirstNineteenPoints() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"),new BigInteger("1"));
        ECCPoint nextPoint = firstPoint.twice();

        // Test 3P: 10, 6
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 10, 6);

        // Test 4P: 3, 1
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 3, 1);

        // Test 5P: 9, 16
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 9, 16);

        // Test 6P: 16, 13
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 16, 13);

        // Test 7P: 0, 6
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 0, 6);

        // Test 8P: 13, 7
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 13, 7);

        // Test 9P: 7, 6
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 7, 6);

        // Test 10P: 7, 11
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 7, 11);

        // Test 11P: 13, 10
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 13, 10);

        // Test 12P: 0, 11
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 0, 11);

        // Test 13P: 16, 4
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 16, 4);

        // Test 14P: 9, 1
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 9, 1);

        // Test 15P: 3, 16
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 3, 16);

        // Test 16P: 10, 11
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 10, 11);

        // Test 17P: 6, 14
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 6, 14);

        // Test 17P: 5, 16
        nextPoint = nextPoint.add(firstPoint);
        validatePoint(nextPoint, 5, 16);
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

