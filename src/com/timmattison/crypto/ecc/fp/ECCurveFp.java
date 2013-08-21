package com.timmattison.crypto.ecc.fp;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.*;

import java.math.BigInteger;

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
    private ECCFieldElement a;
    private ECCFieldElement b;

    public ECCurveFp() {
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

    @AssistedInject
    public ECCurveFp(ECCPointFactory eccPointFactory, ECCFieldElementFactory eccFieldElementFactory, @Assisted("p") BigInteger p, @Assisted("a") BigInteger a, @Assisted("b") BigInteger b) {
        this.eccPointFactory = eccPointFactory;
        this.eccFieldElementFactory = eccFieldElementFactory;

        this.p = p;
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
        return (this.p.equals(other.getP()) && this.a.equals(other.getA()) && this.b.equals(other.getB()));
    }

    public ECCPoint getInfinity() {
        if (infinity == null) {
            eccPointFactory.create(this, null, null);
        }

        return infinity;
    }

    public ECCFieldElement fromBigInteger(BigInteger x) {
        return eccFieldElementFactory.create(this.p, x);
    }

    // for now, work with hex strings because they're easier in JS
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
    public ECCFieldType getECCFieldType() {
        return ECCFieldType.Fp;
    }
}
