package com.timmattison.crypto.ecc;

import com.google.inject.assistedinject.Assisted;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCPointFactory {
    ECCPoint create(@Assisted ECCCurve eccCurve, @Assisted("x") ECCFieldElement x, @Assisted("y") ECCFieldElement y, @Assisted BigInteger z);
}
