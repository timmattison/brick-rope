package com.timmattison.crypto.ecc.fp;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;
import com.timmattison.crypto.ecc.ECCParameters;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class X9ECParameters implements ECCParameters {
    private ECCurveFp curve;
    private ECPointFp g;
    private BigInteger n;
    private BigInteger h;

    public X9ECParameters() {
    }

    @AssistedInject
    public X9ECParameters(@Assisted ECCurveFp curve, @Assisted ECPointFp g, @Assisted("n") BigInteger n, @Assisted("h") BigInteger h) {
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

    public BigInteger getN() {
        return this.n;
    }

    public BigInteger getH() {
        return this.h;
    }
}
