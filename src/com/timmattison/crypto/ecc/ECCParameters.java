package com.timmattison.crypto.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCParameters {
    public ECCCurve getCurve();

    public ECCPoint getG();

    public BigInteger getN();

    public BigInteger getH();
}
