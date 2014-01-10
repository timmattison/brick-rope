package com.timmattison.ecc.random;

import com.timmattison.crypto.ecc.random.interfaces.BigIntegerRandom;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 6:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class BigIntegerRandomForTesting implements BigIntegerRandom {
    private BigInteger valueToReturn;

    public BigIntegerRandomForTesting() {
    }

    public void setValueToReturn(BigInteger valueToReturn) {
        this.valueToReturn = valueToReturn;
    }

    @Override
    public BigInteger getNext(BigInteger lessThanOrEqualTo) {
        checkThatValueWasInitialized();

        return valueToReturn;
    }

    @Override
    public BigInteger getNext(int numberOfBits) {
        checkThatValueWasInitialized();

        return valueToReturn;
    }

    private void checkThatValueWasInitialized() {
        if (valueToReturn == null) {
            throw new UnsupportedOperationException("Value to return was not initialized");
        }
    }
}
