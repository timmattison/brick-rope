package com.timmattison.cryptocurrency.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:32 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockHeader extends Buildable {
    Hash getHash();

    byte[] getPreviousBlockHash();

    byte[] getMerkleRoot();

    Target getTarget();

    BigInteger getHashBigInteger();
}
