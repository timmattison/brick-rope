package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Target;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/9/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTarget implements Target<BigInteger> {
    private final BigInteger target;

    public BitcoinTarget(BigInteger target) {
        this.target = target;
    }

    @Override
    public boolean isBelowHash(BigInteger value) {
        String targetString = target.toString(16);
        String valueString = value.toString(16);
        int targetStringLength = targetString.length();
        int valueStringLength = valueString.length();

        if(value.compareTo(target) < 0) {
            return true;
        }
        else {
            return false;
        }
    }
}
