package com.timmattison.bitcoin.old.ecc.fp;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/21/13
 * Time: 7:22 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECSignatureFp {
    private final BigInteger r;
    private final BigInteger s;
    private final ECPointFp Qu;
    private final X9ECParameters x9ECParameters;

    public ECSignatureFp(X9ECParameters x9ECParameters, BigInteger r, BigInteger s, ECPointFp Qu) {
        this.x9ECParameters = x9ECParameters;
        this.r = r;
        this.s = s;
        this.Qu = Qu;
    }

    public BigInteger getR() {
        return r;
    }

    public BigInteger getS() {
        return s;
    }

    public X9ECParameters getX9ECParameters() {
        return x9ECParameters;
    }

    public BigInteger getN() {
        return x9ECParameters.getN();
    }

    public ECPointFp getG() {
        return x9ECParameters.getG();
    }

    public ECPointFp getQu() {
        return Qu;
    }
}
