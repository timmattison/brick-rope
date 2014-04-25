package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.*;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

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

    @AssistedInject
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
        switch (Integer.parseInt(s.substring(0, 2), 16)) {
            // first byte
            case 0:
                return getInfinity();
            case 2:
            case 3:
                // point compression not supported yet
                return null;
            case 4:
            case 6:
            case 7:
                int start = 2;
                int end = s.length();
                int middle = (end - start) / 2;
                String xHex = s.substring(start, middle + start);
                String yHex = s.substring(middle + start, end);
                return eccPointFactory.create(this,
                        this.fromBigInteger(new BigInteger(xHex, 16)),
                        this.fromBigInteger(new BigInteger(yHex, 16)));
            default:
                // unsupported
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
