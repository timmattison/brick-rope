package com.timmattison.bitcoin.test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 *
 * This information was pulled from https://en.bitcoin.it/wiki/Block
 */
public class Block {
    // Used for sanity check
    private static final long requiredMagicNumber = 0xD9B4BEF9;

    private static final int magicNumberLengthInBytes = 4;
    private static final int blockSizeLengthInBytes = 4;
    private static final int blockHeaderLengthInBytes = 80;
    private static final int transactionCounterMinimumLengthInBytes = 1;
    private static final int transactionCounterMaximumLengthInBytes = 9;

    private byte[] magicNumber;
    private byte[] blockSize;
    private byte[] blockHeader;
    private byte[] transactionCounter;

    private List<Transaction> transactions;
}
