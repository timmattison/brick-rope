package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.exceptions.ScriptExecutionException;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinWordFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.Script;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
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
     * @param inputStream
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
     * @param inputStream
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

        ByteArrayInputStream byteStream = new ByteArrayInputStream(scriptBytes);

        // Is this the coinbase?
        if (coinbase) {
            // Yes, is this a version 2 block?
            if (versionNumber == 2) {
                // Yes, process the additional fields

                // Get the length of the block height value that is coming up
                int lengthOfBlockHeight = (int) EndiannessHelper.ToRealByte((byte) byteStream.read());

                // Read the block height
                byte[] blockHeightBytes = new byte[lengthOfBlockHeight];
                byteStream.read(blockHeightBytes, 0, lengthOfBlockHeight);

                // Convert the block height bytes into a number
                blockHeight = (int) EndiannessHelper.BytesToValue(blockHeightBytes);
            } else if (versionNumber == 1) {
                // No, this is a version 1 block, do nothing
            } else {
                // Unknown version number
                throw new UnsupportedOperationException("Expected version 1 or version 2, saw " + versionNumber);
            }

            // Return immediately
            return Arrays.copyOfRange(data, position, data.length);
        }

        int innerPosition = 0;

        try {
            while (innerPosition < lengthInBytes) {
                // Get the next byte
                byte currentByte = (byte) byteStream.read();
                position++;

                // Get the word that the next byte corresponds to
                Word currentWord = getWordFactory().getWordByOpcode(currentByte);

                // Is this a byte consuming word?
                if (ByteConsumingWord.class.isAssignableFrom(currentWord.getClass())) {
                    // Yes, consume the bytes
                    ByteConsumingWord byteConsumingWord = ((ByteConsumingWord) currentWord);
                    byteConsumingWord.consumeInput(byteStream);
                    position += byteConsumingWord.getInputBytesRequired();
                }

                // Is this a code separator?
                if (OpCodeSeparator.class.isAssignableFrom(currentWord.getClass())) {
                    // Yes, store its position
                    ((OpCodeSeparator) currentWord).setPosition(position);
                }

                // Add the word to our word list
                words.add(currentWord);
            }
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        // Are there too many words?
        if (words.size() > MAX_WORD_LIST_LENGTH) {
            // Yes, throw an exception
            throw new UnsupportedOperationException("The maximum number of words in a script is " + MAX_WORD_LIST_LENGTH + ", saw " + words.size() + " word(s)");
        }
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            if (input) {
                stringBuilder.append("Input");
            } else {
                stringBuilder.append("Output");
            }

            stringBuilder.append(" script #");
            stringBuilder.append(scriptNumber);
            stringBuilder.append(" data:\n");
        }

        int counter = 0;

        // Is this the coinbase?
        if (coinbase) {
            // Yes, no words are stored for the coinbase.  Just dump the bytes.
            stringBuilder.append("Coinbase bytes: ");
            stringBuilder.append(ByteArrayHelper.formatArray(scriptBytes));
            stringBuilder.append("\n");
        }

        for (Word word : words) {
            if (pretty) {
                stringBuilder.append("Opcode #");
                stringBuilder.append(counter);
                stringBuilder.append(": ");
                stringBuilder.append(word.getWord());
                stringBuilder.append(" - [");
            }

            stringBuilder.append(ByteArrayHelper.toHex(word.getOpcode()));

            if (pretty) {
                stringBuilder.append("] ");
            }

            if (word instanceof ByteConsumingWord) {
                ByteConsumingWord byteConsumingWord = (ByteConsumingWord) word;

                if (byteConsumingWord.getInputBytesRequired() != 0) {
                    stringBuilder.append(ByteArrayHelper.formatArray(byteConsumingWord.getInput()));
                }
            }

            stringBuilder.append("\n");
            counter++;
        }

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        return scriptBytes;
    }

    private BitcoinWordFactory getWordFactory() throws IllegalAccessException, InstantiationException {
        if (wordFactory == null) {
            wordFactory = new BitcoinWordFactory();
        }

        return wordFactory;
    }

    public List<Word> getWords() {
        return words;
    }

    public int getVersionNumber() {
        return versionNumber;
    }

    public void removeCodeSeparators() throws IOException {
        // Has the word list been built?
        if (words == null) {
            // No, build it
            build();
        }

        List<Word> tempWords = new ArrayList<Word>();

        // Loop through all of the words
        for (Word word : words) {
            // Is this a code separator?
            if (!OpCodeSeparator.class.isAssignableFrom(word.getClass())) {
                // No, add it to the list
                tempWords.add(word);
            }
        }

        // Use this as our new word list
        words = tempWords;
    }

    public void setCurrentTransaction(Transaction currentTransaction) {
        this.currentTransaction = currentTransaction;
    }

    public Transaction getCurrentTransaction() {
        return currentTransaction;
    }

    public Transaction getReferencedTransaction() {
        return referencedTransaction;
    }

    public void setReferencedTransaction(Transaction referencedTransaction) {
        this.referencedTransaction = referencedTransaction;
    }

    public int getReferencedOutputIndex() {
        return referencedOutputIndex;
    }

    public void setReferencedOutputIndex(int referencedOutputIndex) {
        this.referencedOutputIndex = referencedOutputIndex;
    }
}
