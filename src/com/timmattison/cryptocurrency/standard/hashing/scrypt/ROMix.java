package com.timmattison.cryptocurrency.standard.hashing.scrypt;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 7:18 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ROMix {
    byte[] execute(byte[] input, int blockSize, int cpuMemoryCost);
}
