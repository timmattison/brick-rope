package com.timmattison.crypto.ecc.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/20/13
 * Time: 8:20 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCParameters extends ECCElement {
    /**
     * The curve to which this is being applied
     *
     * @return
     */
    public ECCCurve getCurve();

    /**
     * The base point
     *
     * @return
     */
    public ECCPoint getG();

    /**
     * The order n of G
     *
     * @return
     */
    public BigInteger getN();

    /**
     * The cofactor
     *
     * @return
     */
    public BigInteger getH();
}
