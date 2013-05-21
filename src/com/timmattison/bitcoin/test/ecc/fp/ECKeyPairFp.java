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
    private final BigInteger dU;
    private final ECPointFp Qu;
    private final X9ECParameters x9ECParameters;

    public ECKeyPairFp(X9ECParameters x9ECParameters, BigInteger dU) {
        this.x9ECParameters = x9ECParameters;
        this.dU = dU;

        // Calculate Qu = (xU, yU) = dU * G
        this.Qu = this.x9ECParameters.getG().multiply(dU);
    }

    public BigInteger getDu() {
        return dU;
    }

    public ECPointFp getQu() {
        return Qu;
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
