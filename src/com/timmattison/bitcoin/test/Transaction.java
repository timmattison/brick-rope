package com.timmattison.bitcoin.test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:42 AM
 *
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions
 */
public class Transaction {
    // Used for sanity check
    private static final long currentVersionNumber = 0xD9B4BEF9;

    private static final int versionNumberLengthInBytes = 4;
    private static final int inCounterMinimumLengthInBytes = 1;
    private static final int inCounterMaximumLengthInBytes = 9;
    private static final int outCounterMinimumLengthInBytes = 1;
    private static final int outCounterMaximumLengthInBytes = 9;
    private static final int lockTimeLengthInBytes = 4;

    private byte[] versionNumber;
    private byte[] inCounter;
    private List<Input> inputs;
    private byte[] outCounter;
    private List<Output> outputs;
    private byte[] lockTime;

    private List<Transaction> transactions;
}
