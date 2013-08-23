package com.timmattison.crypto.ecc.random.impl;

import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;
import com.timmattison.crypto.ecc.random.interfaces.RandomFactory;

import javax.inject.Inject;
import java.math.BigInteger;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 6:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RealBigIntegerRandom implements BigIntegerRandom {
    private final RandomFactory randomFactory;
    private Random random;

    @Inject
    public RealBigIntegerRandom(RandomFactory randomFactory) {
        this.randomFactory = randomFactory;
    }

    @Override
    public BigInteger getNext(BigInteger lessThanOrEqualTo) {
        return new BigInteger(lessThanOrEqualTo.bitLength(), getRandom());
    }

    @Override
    public BigInteger getNext(int numberOfBits) {
        return new BigInteger(numberOfBits, getRandom());
    }

    private Random getRandom() {
        if (random == null) {
            random = randomFactory.create();
        }

        return random;
    }
}
