package com.timmattison.crypto.ecc.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCPoint extends ECCElement {
    public ECCCurve getCurve();

    public ECCFieldElement getX();

    public ECCFieldElement getY();

    public boolean equals(ECCPoint other);

    boolean isInfinity();

    public ECCPoint negate();

    public ECCPoint add(ECCPoint b);

    public ECCPoint twice();

    public ECCPoint multiply(BigInteger k);
}