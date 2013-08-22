package com.timmattison.crypto.ecc;

import com.google.inject.assistedinject.Assisted;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 7:25 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCSignatureFactory {
    ECCSignature create(@Assisted("eccParameters") ECCParameters eccParameters, @Assisted("r") BigInteger r, @Assisted("s") BigInteger s, @Assisted("Qu") ECCPoint qU);
}
