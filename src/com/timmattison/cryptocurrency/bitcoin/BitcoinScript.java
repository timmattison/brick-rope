package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.exceptions.ScriptExecutionException;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.Script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinScript implements Script {
    //private static final int MAX_WORD_LIST_LENGTH = 201;
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    private final byte[] data;
    // These values are not in the script

    // XXX - The input/non-input logic be another class with the common functionality in an abstract super class
    boolean input;

    int scriptNumber;
    private long lengthInBytes;
    private List<Word> words;
    private StateMachine stateMachine;
    private BitcoinWordFactory wordFactory;
    private boolean coinbase;
    // Raw bytes, in order they were pulled from the block chain
    private int versionNumber;
    private int blockHeight;
    /**
     * Script bytes
     */
    private byte[] scriptBytes;
    private Transaction currentTransaction;
    private Transaction referencedTransaction;
    private int referencedOutputIndex;

    /**
     * Create an input script
     *
     * @param data
     * @param lengthInBytes
     * @param coinbase
     * @param scriptNumber
     */
    public BitcoinScript(byte[] data, BitcoinWordFactory wordFactory, BitcoinStateMachine stateMachine, long lengthInBytes, boolean coinbase, int scriptNumber) {
        this.data = data;
        this.wordFactory = wordFactory;
        this.stateMachine = stateMachine;

        this.lengthInBytes = lengthInBytes;
        this.coinbase = coinbase;
        this.scriptNumber = scriptNumber;
        this.input = true;
    }

    /**
     * Create an output script
     *
     * @param data
     * @param lengthInBytes
     * @param scriptNumber
     */
    public BitcoinScript(byte[] data, BitcoinWordFactory wordFactory, BitcoinStateMachine stateMachine, long lengthInBytes, int scriptNumber) {
        this.data = data;
        this.wordFactory = wordFactory;
        this.stateMachine = stateMachine;

        this.lengthInBytes = lengthInBytes;
        this.coinbase = false;
        this.versionNumber = 1;
        this.input = false;
        this.scriptNumber = scriptNumber;
    }

    public boolean execute() throws ScriptExecutionException, IOException {
        // Is this the coinbase?
        if (coinbase) {
            // Yes, nothing to execute
            return true;
        }

        stateMachine.reset();

        // Loop through each word
        for (Word word : words) {
            // Execute the instruction
            word.execute();
        }

        // Pop the top value of the stack
        Object topStackValue = stateMachine.pop();

        // Is the top stack non-zero?
        if (topStackValue != null) {
            // Yes, success!
            // XXX - This needs to check for zero values, not just NULLs
            return true;
        } else {
            // No, failure
            return false;
        }
    }

    @Override
    public byte[] build() {
        words = new ArrayList<Word>();

        // Is there any data?
        if (data.length == 0) {
            // No, throw an exception
            throw new UnsupportedOperationException("The script is empty");
        }

        // Are there enough bytes to support that?
        if (data.length < lengthInBytes) {
            // No, throw an exception
            throw new UnsupportedOperationException("Script is supposed to have " + lengthInBytes + " byte(s) but only " + data.length + " byte(s) are left");
        }

        // Is the length valid?
        if (lengthInBytes == 0) {
            // Possibly not

            // Is this a version 2 script?
            if (versionNumber == 2) {
                // Yes, no version 2 script can be zero bytes
                throw new UnsupportedOperationException("Version 2 scripts cannot be zero bytes long, [coinbase? " + (coinbase ? "Yes" : "No") + "] [version: " + versionNumber + "]");
            }
            // Is this a version 1 coinbase?
            else if ((versionNumber == 1) && (!coinbase)) {
                // No, no version 1 non-coinbase script can be zero bytes
                throw new UnsupportedOperationException("Version 1 scripts cannot be zero bytes long unless they are the coinbase");
            }

            // This is a version 1 coinbase, a zero length script is permitted
            return data;
        }

        int position = 0;

        /**
         * Move the bytes we want into a new list.  This is so we can be sure that a misbehaving opcode doesn't try to
         * read past the end of the script.  NOTE: Technically we only support scripts up to 2GB!
         */
        scriptBytes = Arrays.copyOfRange(data, position, position + (int) lengthInBytes);
        position += lengthInBytes;

        byte[] dataAfterScript = Arrays.copyOfRange(data, position, data.length);

        // Is this the coinbase?
        if (coinbase) {
            // Yes, is this a version 2 block?
            if (versionNumber == 2) {
                // Yes, process the additional fields

                int scriptBytesPosition = 0;

                // Get the length of the block height value that is coming up
                int lengthOfBlockHeight = (int) EndiannessHelper.ToRealByte(scriptBytes[scriptBytesPosition]);
                scriptBytesPosition++;

                // Read the block height
                byte[] blockHeightBytes = Arrays.copyOfRange(scriptBytes, scriptBytesPosition, lengthOfBlockHeight);
                scriptBytesPosition += lengthOfBlockHeight;

                // Convert the block height bytes into a number
                blockHeight = (int) EndiannessHelper.BytesToValue(blockHeightBytes);
            } else if (versionNumber == 1) {
                // No, this is a version 1 block, do nothing
            } else {
                // Unknown version number
                throw new UnsupportedOperationException("Expected version 1 or version 2, saw " + versionNumber);
            }

            // Return immediately
            return dataAfterScript;
        }

        byte[] tempData = Arrays.copyOf(scriptBytes, scriptBytes.length);

        do {
            int tempDataPosition = 0;

            // Build the next word
            byte currentByte = (byte) tempData[tempDataPosition];
            tempDataPosition++;

            // Get the word that the next byte corresponds to
            Word currentWord = wordFactory.createWord(currentByte);
            tempData = currentWord.build(Arrays.copyOfRange(tempData, tempDataPosition, tempData.length));

            // Add the word to our word list
            words.add(currentWord);
        } while ((tempData != null) && (tempData.length > 0));

        // Are there too many words?
        if (words.size() > MAX_WORD_LIST_LENGTH) {
            // Yes, throw an exception
            throw new UnsupportedOperationException("The maximum number of words in a script is " + MAX_WORD_LIST_LENGTH + ", saw " + words.size() + " word(s)");
        }

        // Return the data that is after the script
        return dataAfterScript;
    }
}
