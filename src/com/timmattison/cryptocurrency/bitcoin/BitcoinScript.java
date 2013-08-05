package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.exceptions.ScriptExecutionException;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.factories.WordFactory;
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
public abstract class BitcoinScript implements Script {
    //private static final int MAX_WORD_LIST_LENGTH = 201;
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    // These values are not in the script

    protected long lengthInBytes;
    protected List<Word> words;
    protected WordFactory wordFactory;
    // Raw bytes, in order they were pulled from the block chain
    protected int versionNumber;
    /**
     * Script bytes
     */
    private byte[] scriptBytes;
    private Transaction currentTransaction;
    private Transaction referencedTransaction;
    private int referencedOutputIndex;

    protected abstract boolean isExecutable();

    public boolean execute(StateMachine stateMachine) throws ScriptExecutionException, IOException {
        // Is this executable?
        if (!isExecutable()) {
            // No, nothing to execute
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
    public byte[] build(byte[] data) {
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

        // Validate the length
        validateLength();

        // Is there anything to process?
        if (lengthInBytes == 0) {
            // No, just return the bytes we were given
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

        preprocessScriptBytes(scriptBytes);

        if(!isExecutable()) {
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

    protected abstract void preprocessScriptBytes(byte[] scriptBytes);

    protected abstract void validateLength();
}
