package com.timmattison.crypto.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCFieldElement extends ECCElement {
    public boolean equals(ECCFieldElement other);

    public BigInteger toBigInteger();

    public ECCFieldElement negate();

    public ECCFieldElement add(ECCFieldElement b);

    public ECCFieldElement subtract(ECCFieldElement b);

    public ECCFieldElement multiply(ECCFieldElement b);

    public ECCFieldElement square();

    public ECCFieldElement divide(ECCFieldElement b);

    public BigInteger getQ();

    public BigInteger getX();
}
