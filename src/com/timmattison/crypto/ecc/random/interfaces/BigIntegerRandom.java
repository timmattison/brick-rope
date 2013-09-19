package com.timmattison.crypto.ecc.random.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 6:14 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BigIntegerRandom {
    BigInteger getNext(BigInteger lessThanOrEqualTo);

    BigInteger getNext(int numberOfBits);
}
