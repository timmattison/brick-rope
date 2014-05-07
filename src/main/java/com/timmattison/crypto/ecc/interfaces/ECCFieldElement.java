package com.timmattison.crypto.ecc.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 6:48 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCFieldElement extends ECCElement {
    /**
     * The modulus of the curve that this field element is used on
     *
     * @return
     */
    public BigInteger getQ();

    /**
     * The value of this field element
     *
     * @return
     */
    public BigInteger getX();

    public boolean equals(ECCFieldElement other);

    public BigInteger toBigInteger();

    /**
     * Must be transient for serialization!
     *
     * @return
     */
    public ECCFieldElement negate();

    public ECCFieldElement add(ECCFieldElement b);

    public ECCFieldElement subtract(ECCFieldElement b);

    public ECCFieldElement multiply(ECCFieldElement b);

    /**
     * Must be transient for serialization!
     *
     * @return
     */
    public ECCFieldElement square();

    public ECCFieldElement divide(ECCFieldElement b);
}
