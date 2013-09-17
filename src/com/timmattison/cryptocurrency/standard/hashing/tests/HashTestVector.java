package com.timmattison.cryptocurrency.standard.hashing.tests;

import com.timmattison.cryptocurrency.standard.hashing.HashFunction;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 8:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class HashTestVector {
    private HashFunction hashFunction;
    private byte[] input;
    private int numberOfBits;
    private String output;

    public HashTestVector(HashTester hashTester, byte[] input, int numberOfBits, String output) {
        this.hashFunction = hashTester.getHashFunction();
        this.input = input;
        this.numberOfBits = numberOfBits;
        this.output = output;
    }

    public HashTestVector(HashTester hashTester, String input, String output) {
        this.hashFunction = hashTester.getHashFunction();
        this.input = input.getBytes();
        this.numberOfBits = input.length() * 8;
        this.output = output;
    }

    public void runTest() throws Exception {
        hashFunction.initialize(input, numberOfBits);

        while (!hashFunction.isFinished()) {
            hashFunction.step();
        }

        String actualOutput = hashFunction.getOutput();

        if (!actualOutput.equals(output)) {
            throw new Exception("Hash output does not match.  Expected [" + output + "], saw [" + actualOutput + "]");
        }
    }
}
