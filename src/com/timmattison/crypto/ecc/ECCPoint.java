package com.timmattison.crypto.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:56 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCPoint extends ECCElement {
    public ECCFieldElement getX();

    public ECCFieldElement getY();

    public BigInteger getZ();

    public boolean equals(ECCPoint other);

    boolean isInfinity();

    public ECCPoint negate();

    public ECCPoint add(ECCPoint b);

    public ECCPoint twice();

    public ECCPoint multiply(BigInteger k);

    public ECCPoint multiplyTwo(BigInteger j, ECCPoint x, BigInteger k);
}