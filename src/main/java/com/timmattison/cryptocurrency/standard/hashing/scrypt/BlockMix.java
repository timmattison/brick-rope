package com.timmattison.cryptocurrency.standard.hashing.scrypt;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 6:38 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockMix {
    public byte[][] execute(byte[][] input, int rounds);
}
