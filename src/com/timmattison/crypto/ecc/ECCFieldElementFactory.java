package com.timmattison.crypto.ecc;

import com.google.inject.assistedinject.Assisted;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/16/13
 * Time: 7:08 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCFieldElementFactory {
    ECCFieldElement create(@Assisted("q") BigInteger q, @Assisted("x") BigInteger x);
}
