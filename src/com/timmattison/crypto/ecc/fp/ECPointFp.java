package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.enums.ECCFieldType;
import com.timmattison.crypto.ecc.interfaces.*;

import javax.annotation.Nullable;
import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECPointFp implements ECCPoint {
    private ECCPointFactory eccPointFactory;
    private ECCFieldElementFactory eccFieldElementFactory;
    private ECCCurve curve;
    private ECCFieldElement x;
    private ECCFieldElement y;
    private ECCFieldElement trueX;
    private ECCFieldElement trueY;

    private static final BigInteger two = new BigInteger("2");
    private static final BigInteger three = new BigInteger("3");

    public ECPointFp() {
    }

    @AssistedInject
    public ECPointFp(ECCPointFactory eccPointFactory, ECCFieldElementFactory eccFieldElementFactory, @Assisted("curve") ECCCurve curve, @Nullable @Assisted("x") ECCFieldElement x, @Nullable @Assisted("y") ECCFieldElement y) {
        this.eccPointFactory = eccPointFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;
        this.curve = curve;
        this.x = x;
        this.y = y;
    }

    @Override
    public int hashCode() {
        return getX().toBigInteger().add(getY().toBigInteger()).add(getCurve().getA().toBigInteger()).add(getCurve().getB().toBigInteger()).add(getCurve().getP()).intValue();
    }

    @Override
    public boolean equals(Object obj) {
        // Is this an ECC point?
        if (!(obj instanceof ECCPoint)) {
            // No, then we are not equal
            return false;
        }

        ECCPoint other = (ECCPoint) obj;

        if (other.getCurve().equals(this.curve) && other.getX().equals(this.getX()) && other.getY().equals(this.getY())) {
            // Both the curve and the coordinates match.  They are equal.
            return true;
        } else {
            // Something doesn't match.  They are not equal.
            return false;
        }
    }

    @Override
    public String toString() {
        return "[" + getCurve().toString() + " -> (" + getX().toString() + ", " + getY().toString() + ")]";
    }

    @Override
    public ECCCurve getCurve() {
        return curve;
    }

    @Override
    public ECCFieldElement getX() {
        if (x == null) {
            x = eccFieldElementFactory.create(curve.getP(), BigInteger.ZERO);
        }

        if (trueX == null) {
            trueX = eccFieldElementFactory.create(curve.getP(), x.toBigInteger().mod(curve.getP()));
        }

        return trueX;
    }

    @Override
    public ECCFieldElement getY() {
        if (y == null) {
            y = eccFieldElementFactory.create(curve.getP(), BigInteger.ZERO);
        }

        if (trueY == null) {
            trueY = eccFieldElementFactory.create(curve.getP(), y.toBigInteger().mod(curve.getP()));
        }

        return trueY;
    }

    @Override
    public boolean equals(ECCPoint other) {
        if (other == this) return true;
        if (this.isInfinity()) return other.isInfinity();
        if (other.isInfinity()) return this.isInfinity();

        if (other.getX().equals(this.getX()) && other.getY().equals(this.getY())) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isInfinity() {
        if ((getX().toBigInteger().equals(BigInteger.ZERO)) && (getY().toBigInteger().equals(BigInteger.ZERO)))
            return true;
        else return false;
        //return this.z.equals(BigInteger.ZERO) && !this.y.toBigInteger().equals(BigInteger.ZERO);
    }

    @Override
    public ECCPoint negate() {
        return eccPointFactory.create(curve, getX(), getY().negate());
    }

    @Override
    public ECCPoint add(ECCPoint b) {
        if (this.isInfinity()) return b;
        if (b.isInfinity()) return this;
        // XXX - Do I need additional checks here like the signum check in "twice"

        if (this.equals(b)) {
            return this.twice();
        }

        // Calculate s = (y_2 - y_1) / (x_2 - x_1)
        BigInteger bottom = b.getX().toBigInteger().subtract(getX().toBigInteger());
        BigInteger top = b.getY().toBigInteger().subtract(getY().toBigInteger());

        try {
            // Find the multiplicative inverse of the bottom
            bottom = bottom.modInverse(curve.getP());
        } catch (ArithmeticException ex) {
            // XXX - Big integer was not invertible.  Does this mean that it is infinity?
            return eccPointFactory.create(curve, null, null);
        }

        BigInteger s = top.multiply(bottom);
        s = s.mod(curve.getP());

        return getPointFromS(s, b.getX().toBigInteger());
    }

    @Override
    public ECCPoint twice() {
        if (this.isInfinity()) return this;
        if (this.y.toBigInteger().signum() == 0) return this.curve.getInfinity();

        // Calculate s = ((3 * x_1^2) + a) / (2 * y_1)
        BigInteger bottom = two.multiply(getY().toBigInteger());
        BigInteger top = three.multiply(getX().toBigInteger().pow(2)).add(curve.getA().toBigInteger()).mod(curve.getP());

        try {
            // Find the multiplicative inverse of the bottom
            bottom = bottom.modInverse(curve.getP());
        } catch (ArithmeticException ex) {
            // XXX - Big integer was not invertible.  Does this mean that it is infinity?
            return eccPointFactory.create(curve, null, null);
        }

        BigInteger s = top.multiply(bottom);
        s = s.mod(curve.getP());

        return getPointFromS(s, getX().toBigInteger());
    }

    private ECCPoint getPointFromS(BigInteger s, BigInteger x_2) {
        // Calculate x3 = s^2 - x_1 - x_2
        BigInteger x3 = s.pow(2).subtract(getX().toBigInteger()).subtract(x_2).mod(curve.getP());

        // Calculate y3 = s * (x_1 - x_3) - y_1
        BigInteger y3 = getX().toBigInteger().subtract(x3).multiply(s).subtract(getY().toBigInteger()).mod(curve.getP());

        return eccPointFactory.create(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3));
    }

    @Override
    public ECCPoint multiply(BigInteger d) {
        ECCPoint q = eccPointFactory.create(curve, curve.fromBigInteger(BigInteger.ZERO), curve.fromBigInteger(BigInteger.ZERO));

        for (int loop = d.bitLength() - 1; loop >= 0; loop--) {
            q = q.twice();

            if (d.testBit(loop)) {
                q = q.add(this);
            }
        }

        return q;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}