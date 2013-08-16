package com.timmattison.crypto.ecc;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import com.google.inject.assistedinject.AssistedInject;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:30 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCParametersFactory {
    ECCParameters create(@Assisted ECCCurve curve, @Assisted ECCPoint g, @Assisted("n") BigInteger n, @Assisted("h") BigInteger h);
}
