package com.timmattison.cryptocurrency.standard.hashing;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/15/13
 * Time: 4:44 PM
 * To change this template use File | Settings | File Templates.
 */
public interface HashFunction {
    public HashState getHashState();

    public void addHashStateListener(HashStateListener hashStateListener);

    public void initialize(byte[] input, int numberOfBits);

    public boolean isFinished();

    public void step();

    public String getOutput();

    public String getName();
}
