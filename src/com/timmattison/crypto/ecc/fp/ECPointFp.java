package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.bitcoin.test.BigIntegerHelper;
import com.timmattison.crypto.ecc.*;

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
    private ECCCurve curve;
    private ECCFieldElement x;
    private ECCFieldElement y;
    private BigInteger z;
    private BigInteger zinv;
    private ECCPointFactory eccPointFactory;

    public ECPointFp() {
    }

    @AssistedInject
    public ECPointFp(ECCPointFactory eccPointFactory, @Assisted("curve") ECCCurve curve, @Nullable @Assisted("x") ECCFieldElement x, @Nullable @Assisted("y") ECCFieldElement y, @Nullable @Assisted("z") BigInteger z) {
        this.eccPointFactory = eccPointFactory;
        this.curve = curve;
        this.x = x;
        this.y = y;
        // Projective coordinates: either zinv == null or z * zinv == 1
        // z and zinv are just BigIntegers, not fieldElements
        if (z == null) {
            this.z = BigInteger.ONE;
        } else {
            this.z = z;
        }
        this.zinv = null;
        //TODO: compression flag
    }

    public ECCFieldElement getX() {
        if (this.zinv == null) {
            this.zinv = this.z.modInverse(this.curve.getP());
        }
        return this.curve.fromBigInteger(this.x.toBigInteger().multiply(this.zinv).mod(this.curve.getP()));
    }

    public ECCFieldElement getY() {
        if (this.zinv == null) {
            this.zinv = this.z.modInverse(this.curve.getP());
        }
        return this.curve.fromBigInteger(this.y.toBigInteger().multiply(this.zinv).mod(this.curve.getP()));
    }

    public boolean equals(ECCPoint other) {
        if (other == this) return true;
        if (this.isInfinity()) return other.isInfinity();
        if (other.isInfinity()) return this.isInfinity();
        BigInteger u, v;
        // u = Y2 * Z1 - Y1 * Z2
        u = other.getY().toBigInteger().multiply(this.z).subtract(this.y.toBigInteger().multiply(other.getZ())).mod(this.curve.getP());
        if (!u.equals(BigInteger.ZERO)) return false;
        // v = X2 * Z1 - X1 * Z2
        v = other.getX().toBigInteger().multiply(this.z).subtract(this.x.toBigInteger().multiply(other.getZ())).mod(this.curve.getP());
        return v.equals(BigInteger.ZERO);
    }

    public boolean isInfinity() {
        if ((this.x == null) && (this.y == null)) return true;
        return this.z.equals(BigInteger.ZERO) && !this.y.toBigInteger().equals(BigInteger.ZERO);
    }

    public ECCPoint negate() {
        return eccPointFactory.create(this.curve, this.x, this.y.negate(), this.z);
    }

    public ECCPoint add(ECCPoint b) {
        if (this.isInfinity()) return b;
        if (b.isInfinity()) return this;
        // u = Y2 * Z1 - Y1 * Z2
        BigInteger u = b.getY().toBigInteger().multiply(this.z).subtract(this.y.toBigInteger().multiply(b.getZ())).mod(this.curve.getP());
        // v = X2 * Z1 - X1 * Z2
        BigInteger v = b.getX().toBigInteger().multiply(this.z).subtract(this.x.toBigInteger().multiply(b.getZ())).mod(this.curve.getP());
        if (BigInteger.ZERO.equals(v)) {
            if (BigInteger.ZERO.equals(u)) {
                return this.twice(); // this == b, so double
            }
            return this.curve.getInfinity(); // this = -b, so infinity
        }
        BigInteger THREE = new BigInteger("3");
        BigInteger x1 = this.x.toBigInteger();
        BigInteger y1 = this.y.toBigInteger();
        BigInteger x2 = b.getX().toBigInteger();
        BigInteger y2 = b.getY().toBigInteger();
        BigInteger v2 = BigIntegerHelper.squareBigInteger(v);
        BigInteger v3 = v2.multiply(v);
        BigInteger x1v2 = x1.multiply(v2);
        BigInteger zu2 = BigIntegerHelper.squareBigInteger(u).multiply(this.z);

        // x3 = v * (z2 * (z1 * u^2 - 2 * x1 * v^2) - v^3)
        BigInteger x3 = zu2.subtract(x1v2.shiftLeft(1)).multiply(b.getZ()).subtract(v3).multiply(v).mod(this.curve.getP());
        // y3 = z2 * (3 * x1 * u * v^2 - y1 * v^3 - z1 * u^3) + u * v^3
        BigInteger y3 = x1v2.multiply(THREE).multiply(u).subtract(y1.multiply(v3)).subtract(zu2.multiply(u)).multiply(b.getZ()).add(u.multiply(v3)).mod(this.curve.getP());
        // z3 = v^3 * z1 * z2
        BigInteger z3 = v3.multiply(this.z).multiply(b.getZ()).mod(this.curve.getP());

        return eccPointFactory.create(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3);
    }

    public ECCPoint twice() {
        if (this.isInfinity()) return this;
        if (this.y.toBigInteger().signum() == 0) return this.curve.getInfinity();
        // TODO: optimized handling of constants
        BigInteger THREE = new BigInteger("3");
        BigInteger x1 = this.x.toBigInteger();
        BigInteger y1 = this.y.toBigInteger();
        BigInteger y1z1 = y1.multiply(this.z);
        BigInteger y1sqz1 = y1z1.multiply(y1).mod(this.curve.getP());
        BigInteger a = this.curve.getA().toBigInteger();
        // w = 3 * x1^2 + a * z1^2
        BigInteger w = BigIntegerHelper.squareBigInteger(x1).multiply(THREE);
        if (!BigInteger.ZERO.equals(a)) {
            w = w.add(BigIntegerHelper.squareBigInteger(this.z).multiply(a));
        }
        w = w.mod(this.curve.getP());
        // x3 = 2 * y1 * z1 * (w^2 - 8 * x1 * y1^2 * z1)
        BigInteger x3 = BigIntegerHelper.squareBigInteger(w).subtract(x1.shiftLeft(3).multiply(y1sqz1)).shiftLeft(1).multiply(y1z1).mod(this.curve.getP());
        // y3 = 4 * y1^2 * z1 * (3 * w * x1 - 2 * y1^2 * z1) - w^3
        BigInteger y3 = w.multiply(THREE).multiply(x1).subtract(y1sqz1.shiftLeft(1)).shiftLeft(2).multiply(y1sqz1).subtract(BigIntegerHelper.squareBigInteger(w).multiply(w)).mod(this.curve.getP());
        // z3 = 8 * (y1 * z1)^3
        BigInteger z3 = BigIntegerHelper.squareBigInteger(y1z1).multiply(y1z1).shiftLeft(3).mod(this.curve.getP());
        return eccPointFactory.create(this.curve, this.curve.fromBigInteger(x3), this.curve.fromBigInteger(y3), z3);
    }

    // Simple NAF (Non-Adjacent Form) multiplication algorithm
    // TODO: modularize the multiplication algorithm
    public ECCPoint multiply(BigInteger k) {
        if (this.isInfinity()) return this;
        if (k.signum() == 0) return this.curve.getInfinity();
        BigInteger e = k;
        BigInteger h = e.multiply(new BigInteger("3"));
        ECCPoint neg = this.negate();
        ECCPoint R = this;
        for (int i = h.bitLength() - 2; i > 0; --i) {
            R = R.twice();
            boolean hBit = h.testBit(i);
            boolean eBit = e.testBit(i);
            if (hBit != eBit) {
                R = R.add(hBit ? this : neg);
            }
        }
        return R;
    }

    // Compute this*j + x*k (simultaneous multiplication)
    public ECCPoint multiplyTwo(BigInteger j, ECCPoint x, BigInteger k) {
        int i;
        if (j.bitLength() > k.bitLength())
            i = j.bitLength() - 1;
        else
            i = k.bitLength() - 1;
        ECCPoint R = this.curve.getInfinity();
        ECCPoint both = this.add(x);
        while (i >= 0) {
            R = R.twice();
            if (j.testBit(i)) {
                if (k.testBit(i)) {
                    R = R.add(both);
                } else {
                    R = R.add(this);
                }
            } else {
                if (k.testBit(i)) {
                    R = R.add(x);
                }
            }
            --i;
        }
        return R;
    }

    @Override
    public BigInteger getZ() {
        return z;
    }

    @Override
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}