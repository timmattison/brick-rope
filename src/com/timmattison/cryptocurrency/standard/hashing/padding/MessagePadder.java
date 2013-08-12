package com.timmattison.cryptocurrency.standard.hashing.padding;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 11:13 PM
 * To change this template use File | Settings | File Templates.
 */
public interface MessagePadder {
    byte[] pad(byte[] input, int messageBitLength);

    int getBitLengthWithPadding();

    int getModulus();
}
