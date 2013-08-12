package com.timmattison.cryptocurrency.interfaces;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 6:48 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Hash {
    byte[] getInput();

    byte[] getOutput();

    BigInteger getOutputBigInteger();
}
