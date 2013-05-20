package com.timmattison.bitcoin.test.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECCurveFp {
    private final ECPointFp infinity;
    private BigInteger q;
    private ECFieldElementFp a;
    private ECFieldElementFp b;

    public ECCurveFp(BigInteger q, BigInteger a, BigInteger b) {
        this.q = q;
        this.a = this.fromBigInteger(a);
        this.b = this.fromBigInteger(b);
        this.infinity = new ECPointFp(this, null, null, null);
    }

    public BigInteger getQ() {
        return this.q;
    }

    public ECFieldElementFp getA() {
        return this.a;
    }

    public ECFieldElementFp getB() {
        return this.b;
    }

    public boolean equals(ECCurveFp other) {
        if (other == this) return true;
        return (this.q.equals(other.q) && this.a.equals(other.a) && this.b.equals(other.b));
    }

    public ECPointFp getInfinity() {
        return this.infinity;
    }

    public ECFieldElementFp fromBigInteger(BigInteger x) {
        return new ECFieldElementFp(this.q, x);
    }

    // for now, work with hex strings because they're easier in JS
    public ECPointFp decodePointHex(String s) {
        switch (Integer.parseInt(s.substring(0, 2), 16)) {
            // first byte
            case 0:
                return this.infinity;
            case 2:
            case 3:
                // point compression not supported yet
                return null;
            case 4:
            case 6:
            case 7:
                int len = (s.length() - 2) / 2;
                String xHex = s.substring(2, len);
                String yHex = s.substring(len + 2, len);
                return new ECPointFp(this,
                        this.fromBigInteger(new BigInteger(xHex, 16)),
                        this.fromBigInteger(new BigInteger(yHex, 16)), null);
            default:
                // unsupported
                return null;
        }
    }
}
