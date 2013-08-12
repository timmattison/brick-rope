package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinTarget;
import com.timmattison.cryptocurrency.interfaces.TargetFactory;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/9/13
 * Time: 6:35 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTargetFactory implements TargetFactory<BigInteger> {
    public BitcoinTarget create(BigInteger targetBigInteger) {
        return new BitcoinTarget(targetBigInteger);
    }
}
