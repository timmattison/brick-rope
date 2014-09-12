package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Random;

// This code is based off of the Javascript implementation found here - http://www-cs-students.stanford.edu/~tjw/jsbn/

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECCurveFp implements ECCCurve {
    private ECCPointFactory eccPointFactory;
    private ECCFieldElementFactory eccFieldElementFactory;
    private ECCPoint infinity;
    private BigInteger p;
    private BigInteger order;
    private ECCFieldElement a;
    private ECCFieldElement b;

    // Required or Guice throws exceptions
    public ECCurveFp() {
    }

    @Inject
    public ECCurveFp(ECCPointFactory eccPointFactory, ECCFieldElementFactory eccFieldElementFactory, @Assisted("p") BigInteger p, @Assisted("order") BigInteger order, @Assisted("a") BigInteger a, @Assisted("b") BigInteger b) {
        this.eccPointFactory = eccPointFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;

        this.p = p;
        this.order = order;
        this.a = this.fromBigInteger(a);
        this.b = this.fromBigInteger(b);
    }

    public BigInteger getP() {
        return this.p;
    }

    public ECCFieldElement getA() {
        return this.a;
    }

    public ECCFieldElement getB() {
        return this.b;
    }

    public boolean equals(ECCCurve other) {
        if (other == this) return true;
        return (this.getP().equals(other.getP()) && this.getA().equals(other.getA()) && this.getB().equals(other.getB()));
    }

    public ECCPoint getInfinity() {
        if (infinity == null) {
            infinity = eccPointFactory.create(this, null, null);
        }

        return infinity;
    }

    @Override
    public ECCPoint generateBasePoint(Random random) {
        // http://stackoverflow.com/questions/11156779/generate-base-point-g-of-elliptic-curve-for-elliptic-curve-cryptography
        boolean found = false;

        ECCPoint basePoint = null;

        while (!found) {
            // Figure out how many bits are in our modulus so we generate values that are of the same scale
            double bitsInP = Math.log(getP().doubleValue()) / Math.log(2);

            ECCFieldElement x = fromBigInteger(new BigInteger((int) bitsInP, random));
            ECCFieldElement y = fromBigInteger(new BigInteger((int) bitsInP, random));

            // Create a random base point
            basePoint = eccPointFactory.create(this, x, y);

            // Find a large prime
            BigInteger largePrime = BigInteger.probablePrime((int) bitsInP, random);

            // large prime * small factor must equal curve order
            // So small factor = curve order * (large prime ^ -1)
            BigInteger smallFactor = getOrder().multiply(largePrime.modInverse(getP()));

            // testPoint1 * smallFactor
            ECCPoint testPoint1 = basePoint.multiply(smallFactor);

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

            // If testPoint2 == 0 then the curve does not have a point of the order of small factor * large prime
            if (testPoint2.getX().toBigInteger().equals(BigInteger.ZERO) && (testPoint2.getY().toBigInteger().equals(BigInteger.ZERO))) {
                // No good.  Curve did not have order small factor * large prime.
                return null;
            }
        }

        return basePoint;
    }

    public ECCFieldElement fromBigInteger(BigInteger x) {
        return eccFieldElementFactory.create(getP(), x);
    }

    // for now, work with hex strings because they're easier in JS
    @Override
    public ECCPoint decodePointHex(String s) {
        int start = 2;
        int end = s.length();
        String xHex;

        // Convert the first byte
        int type = Integer.parseInt(s.substring(0, start), 16);

        switch (type) {
            case 0:
                return getInfinity();
            case 2:
                // Y is even
                xHex = s.substring(start, end);
                return decompressPoint(false, new BigInteger(xHex, 16));
            case 3:
                // Y is odd
                xHex = s.substring(start, end);
                return decompressPoint(true, new BigInteger(xHex, 16));
            case 4:
            case 6:
            case 7:
                end = s.length();
                int middle = (end - start) / 2;
                xHex = s.substring(start, middle + start);
                String yHex = s.substring(middle + start, end);
                return eccPointFactory.create(this,
                        this.fromBigInteger(new BigInteger(xHex, 16)),
                        this.fromBigInteger(new BigInteger(yHex, 16)));
            default:
                // unsupported
                return null;
        }
    }

    private ECCPoint decompressPoint(boolean odd, BigInteger x) {
        // Some guidance from: https://bitcointalk.org/index.php?topic=162805.0

        // Get Q from ECCFieldElement a
        BigInteger q = a.getQ();

        // Is q = 3 mod 4?
        if (q.mod(BigInteger.valueOf(4)).equals(BigInteger.valueOf(3))) {
            // Yes, our algorithm can handle this

            // y^2 = x^3 + ax^2 + b, so we need to perform sqrt to recover y
            BigInteger ySquared = x.multiply(x.multiply(x).add(a.toBigInteger())).add(b.toBigInteger());

            // sqrt(a) = a^((q+1)/4) if q = 3 mod 4
            BigInteger y = ySquared.modPow(q.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), q);

            // Is y even but we expect it to be odd?
            if (y.testBit(0) != odd) {
                // Yes, subtract y from q since we must have the other solution
                y = q.subtract(y);
            }

            // Create our point from the x and y values
            return eccPointFactory.create(this,
                    this.fromBigInteger(x),
                    this.fromBigInteger(y));
        } else {
            // No, our algorithm cannot handle the case where q != 3 mod 4
            return null;
        }
    }

    @Override
    public ECCPoint decodePointBinary(byte[] point) {
        return decodePointHex(ByteArrayHelper.toHex(point));
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }

    @Override
    public boolean equals(Object obj) {
        // Is the other object an ECCCurve?
        if (!(obj instanceof ECCCurve)) {
            return false;
        }

        ECCCurve other = (ECCCurve) obj;

        if (getP().equals(other.getP()) && getA().equals(other.getA()) && getB().equals(other.getB())) {
            // All of the parameters are equal.  They are equal.
            return true;
        } else {
            // Something didn't match.  They are not equal.
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + getP().toString() + ", " + getA().toString() + ", " + getB().toString() + "]";
    }

    @Override
    public BigInteger getOrder() {
        return order;
    }
}
