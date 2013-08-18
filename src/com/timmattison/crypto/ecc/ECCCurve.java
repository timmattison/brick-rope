package com.timmattison.crypto.ecc;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 7:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCCurve extends ECCElement {
    public BigInteger getP();

    public ECCFieldElement getA();

    public ECCFieldElement getB();

    public boolean equals(ECCCurve other);

    public ECCPoint getInfinity();

    public ECCFieldElement fromBigInteger(BigInteger x);

    // for now, work with hex strings because they're easier in JS
    public ECCPoint decodePointHex(String s);
}
