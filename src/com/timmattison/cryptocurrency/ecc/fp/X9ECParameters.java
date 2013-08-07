package com.timmattison.cryptocurrency.ecc.fp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class X9ECParameters {
    private final ECCurveFp curve;
    private final ECPointFp g;
    private final BigInteger n;
    private final BigInteger h;

    public X9ECParameters(ECCurveFp curve, ECPointFp g, BigInteger n, BigInteger h) {
        this.curve = curve;
        this.g = g;
        this.n = n;
        this.h = h;
    }

    public ECCurveFp getCurve() {
        return this.curve;
    }

    public ECPointFp getG() {
        return this.g;
    }

    public BigInteger getN()
    {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }
}
