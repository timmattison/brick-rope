package com.timmattison.bitcoin.test;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 *
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_output_of_a_transaction_-_Txout
 */
public class Output {
    private static final int valueLengthInBytes = 8;
    private static final int outputScriptMinimumLengthInBytes = 1;
    private static final int outputScriptMaximumLengthInBytes = 9;

    private byte[] value;
    private byte[] outputScriptLength;
    private Script outputScript;
}
