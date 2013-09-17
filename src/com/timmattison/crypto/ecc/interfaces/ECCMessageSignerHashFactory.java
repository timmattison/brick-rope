package com.timmattison.crypto.ecc.interfaces;

import com.google.inject.assistedinject.Assisted;
import com.timmattison.cryptocurrency.interfaces.Hash;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/22/13
 * Time: 6:58 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ECCMessageSignerHashFactory {
    Hash create(@Assisted("input") byte[] input);
}
