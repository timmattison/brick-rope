package com.timmattison.crypto.ecc.interfaces;

import com.google.inject.assistedinject.Assisted;
import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;

import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:05 AM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCMessageSignerFactory {
    ECCMessageSigner create(@Assisted("bigIntegerRandom") BigIntegerRandom bigIntegerRandom, @Assisted("eccKeyPair") ECCKeyPair eccKeyPair);
}
