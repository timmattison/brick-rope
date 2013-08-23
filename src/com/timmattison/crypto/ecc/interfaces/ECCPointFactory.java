package com.timmattison.crypto.ecc.interfaces;

import com.google.inject.assistedinject.Assisted;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:03 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCPointFactory {
    ECCPoint create(@Assisted("curve") ECCCurve eccCurve, @Assisted("x") ECCFieldElement x, @Assisted("y") ECCFieldElement y);
}
