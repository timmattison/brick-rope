package com.timmattison.crypto.ecc;

import com.google.inject.assistedinject.Assisted;
import com.google.inject.internal.util.$Nullable;

import javax.annotation.Nullable;
import java.math.BigInteger;

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
