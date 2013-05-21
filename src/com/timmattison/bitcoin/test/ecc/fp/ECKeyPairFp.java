package com.timmattison.bitcoin.test.ecc.fp;

import org.bouncycastle.pqc.math.linearalgebra.Vector;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:24 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECKeyPairFp {
    private final BigInteger d;
    private final ECPointFp q;
    private final X9ECParameters x9ECParameters;

    public ECKeyPairFp(X9ECParameters x9ECParameters, BigInteger d) {
        this.x9ECParameters = x9ECParameters;
        this.d = d;

        // Calculate q = (x, y) = d * G
        this.q = this.x9ECParameters.getG().multiply(d);
    }

    public BigInteger getD() {
        return d;
    }

    public ECPointFp getQ() {
        return q;
    }

    public BigInteger getN() {
        return x9ECParameters.getN();
    }

    public ECPointFp getG() {
        return x9ECParameters.getG();
    }

    public X9ECParameters getX9ECParameters() {
        return x9ECParameters;
    }
}
