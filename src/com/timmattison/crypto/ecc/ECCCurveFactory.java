package com.timmattison.crypto.ecc;

import com.google.inject.assistedinject.Assisted;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:29 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCCurveFactory {
    ECCCurve create(@Assisted("p") BigInteger p, @Assisted("a") BigInteger a, @Assisted("b") BigInteger b);
}
