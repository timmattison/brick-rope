package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.*;
import com.timmattison.crypto.ecc.fp.TestCurve;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

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

    //private ECCParameters getSmallCurve2() {
    //    return injector.getInstance(TestCurve.class).getSmallCurve2();
    //}

    private ECCPoint getPoint(BigInteger x, BigInteger y) {
        ECCPointFactory eccPointFactory = injector.getInstance(ECCPointFactory.class);
        ECCCurve curve = getSmallCurve1().getCurve();
        ECCFieldElement xFieldElement = curve.fromBigInteger(x);
        ECCFieldElement yFieldElement = curve.fromBigInteger(y);
        return eccPointFactory.create(curve, xFieldElement, yFieldElement);
    }

    private ECCPoint getBasePoint() {
        return getPoint(new BigInteger("5"), new BigInteger("1"));
    }

    @Test
    public void testMultiplyInfinity() throws Exception {
        ECCParameters smallCurve = getSmallCurve1();
        ECCPoint result = smallCurve.getG().multiply(smallCurve.getN());

        Assert.assertTrue(result.isInfinity());
    }

    @Test
    public void testECDH1() {
        ECCPoint basePoint = getBasePoint();

        BigInteger alicePrivateKey = BigInteger.valueOf(3);
        ECCPoint alicePublicKey = basePoint.multiply(alicePrivateKey);

        BigInteger bobPrivateKey = BigInteger.valueOf(10);
        ECCPoint bobPublicKey = basePoint.multiply(bobPrivateKey);

        ECCPoint aliceJointSecret = bobPublicKey.multiply(alicePrivateKey);
        ECCPoint bobJointSecret = alicePublicKey.multiply(bobPrivateKey);

        Assert.assertTrue(aliceJointSecret.equals(bobJointSecret));
    }

    @Test
    public void testMultiply13P() {
        ECCPoint thirteenthPoint = getBasePoint().multiply(BigInteger.valueOf(13));
        validatePoint(thirteenthPoint, 16, 4);
    }

    @Test
    public void testDoubleBasePoint() {
        ECCPoint result = getBasePoint().twice();

        Assert.assertTrue(result.getX().toBigInteger().equals(new BigInteger("6")));
        Assert.assertTrue(result.getY().toBigInteger().equals(new BigInteger("3")));
    }

    private void validatePoint(ECCPoint point, int x, int y) {
        Assert.assertTrue(point.getX().toBigInteger().equals(BigInteger.valueOf(x)));
        Assert.assertTrue(point.getY().toBigInteger().equals(BigInteger.valueOf(y)));
    }

    @Test
    public void testCalculateUpTo18P() {
        ECCPoint basePoint = getBasePoint();
        ECCPoint nextPoint = getBasePoint().twice();

        // Test 3P: 10, 6
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 10, 6);

        // Test 4P: 3, 1
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 3, 1);

        // Test 5P: 9, 16
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 9, 16);

        // Test 6P: 16, 13
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 16, 13);

        // Test 7P: 0, 6
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 0, 6);

        // Test 8P: 13, 7
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 13, 7);

        // Test 9P: 7, 6
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 7, 6);

        // Test 10P: 7, 11
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 7, 11);

        // Test 11P: 13, 10
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 13, 10);

        // Test 12P: 0, 11
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 0, 11);

        // Test 13P: 16, 4
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 16, 4);

        // Test 14P: 9, 1
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 9, 1);

        // Test 15P: 3, 16
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 3, 16);

        // Test 16P: 10, 11
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 10, 11);

        // Test 17P: 6, 14
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 6, 14);

        // Test 18P: 5, 16
        nextPoint = nextPoint.add(basePoint);
        validatePoint(nextPoint, 5, 16);
    }

    @Test
    public void testCalculate19P() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"), new BigInteger("1"));
        ECCPoint nextPoint = firstPoint.twice();

        for (int loop = 3; loop < 19; loop++) {
            nextPoint = nextPoint.add(firstPoint);
        }

        nextPoint = nextPoint.add(firstPoint);

        Assert.assertTrue(nextPoint.isInfinity());
    }

    @Test
    public void testCalculateFourthPoint() {
        ECCPoint firstPoint = getPoint(new BigInteger("5"), new BigInteger("1"));
        ECCPoint secondPoint = firstPoint.twice();
        ECCPoint thirdPoint = secondPoint.add(firstPoint);
        ECCPoint fourthPoint = thirdPoint.add(firstPoint);

        Assert.assertTrue(fourthPoint.getX().toBigInteger().equals(new BigInteger("3")));
        Assert.assertTrue(fourthPoint.getY().toBigInteger().equals(new BigInteger("1")));
    }

    @Test
    public void testGenerateBasePoint() {
        // http://stackoverflow.com/questions/11156779/generate-base-point-g-of-elliptic-curve-for-elliptic-curve-cryptography
        Random random = new Random(1);
        ECCParameters eccParameters = getSmallCurve1();

        Set<ECCPoint> points = new HashSet<ECCPoint>();

        for (int loop = 0; loop < 256; loop++) {
            boolean found = false;

            while (!found) {
                ECCPoint eccPoint = getPoint(BigInteger.valueOf(random.nextInt(0xFFFFFF)), BigInteger.valueOf(random.nextInt(0xFFFFFF)));

                BigInteger largePrime = BigInteger.probablePrime(256, random);

                // large prime * small factor must equal curve order
                // So small factor = curve order * (large prime ^ -1)
                BigInteger smallFactor = eccParameters.getN().multiply(largePrime.modInverse(eccParameters.getCurve().getP()));

                // testPoint1 * smallFactor
                ECCPoint testPoint1 = eccPoint.multiply(smallFactor);

                // XXX - In the first iteration we get testPoint1 == (6, 0) and junkPoint == (0, 0).  Is junkPoint infinity?
                ECCPoint junkPoint = testPoint1.twice();

                // If testPoint1 == 0 then try again
                if (testPoint1.getX().toBigInteger().equals(BigInteger.ZERO) && (testPoint1.getY().toBigInteger().equals(BigInteger.ZERO))) {
                    // This particular point won't work.  Try again.
                    continue;
                }

                // testPoint1 is good
                found = true;

                // testPoint1 * largePrime
                ECCPoint testPoint2 = testPoint1.multiply(largePrime);

                // If testPoint2 == 0 then the curve does not have order small factor * large prime
                if (testPoint2.getX().toBigInteger().equals(BigInteger.ZERO) && (testPoint2.getY().toBigInteger().equals(BigInteger.ZERO))) {
                    // No good.  Curve did not have order small factor * large prime.
                    Assert.fail("Curve did not have order " + smallFactor + " * " + largePrime);
                }

                points.add(eccPoint);
            }
        }

        for (ECCPoint eccPoint : points) {
            System.out.println(eccPoint);
        }
    }
}
