package com.timmattison.bitcoin.test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:46 AM
 *
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_input_of_a_transaction_-_Txin
 */
public class Input {
    private static final int previousTransactionHashLengthInBytes = 32;
    private static final int previousOutputIndexLengthInBytes = 4;
    private static final int inputScriptMinimumLengthInBytes = 1;
    private static final int inputScriptMaximumLengthInBytes = 9;
    private static final int sequenceNumberLengthInBytes = 4;

    private byte[] previousTransactionHash;
    private byte[] previousOutputIndex;
    private byte[] inputScriptLength;
    private Script inputScript;
    private byte[] sequenceNumber;
}
