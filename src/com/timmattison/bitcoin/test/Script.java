package com.timmattison.bitcoin.test;

import com.timmattison.bitcoin.test.script.ByteConsumingWord;
import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;
import com.timmattison.bitcoin.test.script.words.crypto.OpCodeSeparator;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class Script extends ByteConsumer {
    private static final String name = "SCRIPT";
    //private static final int MAX_WORD_LIST_LENGTH = 201;
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    // These values are not in the script
    boolean input;
    int scriptNumber;
    private long lengthInBytes;
    private List<Word> words;
    private StateMachine stateMachine;
    private WordFactory wordFactory;
    private boolean coinbase;

    // Raw bytes, in order they were pulled from the block chain
    private int versionNumber;
    private int blockHeight;
    /**
     * Script bytes
     */
    private byte[] scriptBytes;

    /**
     * Create an input script
     *
     * @param inputStream
     * @param lengthInBytes
     * @param coinbase
     * @param versionNumber
     * @param scriptNumber
     * @param debug
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public Script(InputStream inputStream, long lengthInBytes, boolean coinbase, int versionNumber, int scriptNumber, boolean debug) throws IllegalAccessException, InstantiationException, IOException {
        super(inputStream, debug);

        this.lengthInBytes = lengthInBytes;
        this.coinbase = coinbase;
        this.versionNumber = versionNumber;
        this.scriptNumber = scriptNumber;
        this.input = true;
    }

    /**
     * Create an output script
     *
     * @param inputStream
     * @param lengthInBytes
     * @param scriptNumber
     * @param debug
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws IOException
     */
    public Script(InputStream inputStream, long lengthInBytes, int scriptNumber, boolean debug) throws IllegalAccessException, InstantiationException, IOException {
        super(inputStream, debug);

        this.lengthInBytes = lengthInBytes;
        this.coinbase = false;
        this.versionNumber = 1;
        this.input = false;
        this.scriptNumber = scriptNumber;
    }

    public Script(ByteArrayInputStream byteArrayInputStream, int versionNumber, boolean debug) throws IOException {
        super(byteArrayInputStream, debug);

        this.lengthInBytes = byteArrayInputStream.available();
        this.versionNumber = versionNumber;

        this.coinbase = false;
        this.input = false;
        this.scriptNumber = -1;
    }

    @Override
    protected String getName() {
        return name;
    }

    public String getListOfWords() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Word word : words) {
            stringBuilder.append(word.getWord());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public boolean execute() throws ScriptExecutionException, IOException {
        // Is this the coinbase?
        if (coinbase) {
            // Yes, nothing to execute
            return true;
        }

        StateMachine localStateMachine = getStateMachine(dumpBytes());

        // Loop through each word
        for (Word word : words) {
            // Execute the instruction
            word.execute(localStateMachine);
        }

        // Pop the top value of the stack
        Object topStackValue = localStateMachine.pop();

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

    private StateMachine getStateMachine(byte[] scriptBytes) throws IOException {
        if (stateMachine == null) {
            stateMachine = new StateMachine();
            stateMachine.setScriptBytes(scriptBytes);
        }

        return stateMachine;
    }

    @Override
    protected void build() throws IOException {
        words = new ArrayList<Word>();

        // Is there any data?
        if ((inputStream == null) || (inputStream.available() == 0)) {
            // No, throw an exception
            throw new UnsupportedOperationException("The script is empty");
        }

        long availableBytes = inputStream.available() & 0xFFFFFFFFL;

        // Are there enough bytes to support that?
        if (availableBytes < lengthInBytes) {
            // No, throw an exception
            throw new UnsupportedOperationException("Script is supposed to have " + lengthInBytes + " byte(s) but only " + inputStream.available() + " byte(s) are left");
        }

        // Is the length valid?
        if (lengthInBytes == 0) {
            // Possibly not

            // Is this a version 2 script?
            if (versionNumber == 2) {
                // Yes, no version 2 script can be zero bytes
                pullDebugBytes();
                throw new UnsupportedOperationException("Version 2 scripts cannot be zero bytes long, [coinbase? " + (coinbase ? "Yes" : "No") + "] [version: " + versionNumber + "]");
            }
            // Is this a version 1 coinbase?
            else if ((versionNumber == 1) && (!coinbase)) {
                // No, no version 1 non-coinbase script can be zero bytes
                pullDebugBytes();
                throw new UnsupportedOperationException("Version 1 scripts cannot be zero bytes long unless they are the coinbase");
            }

            // This is a version 1 coinbase, a zero length script is permitted
            return;
        }

        /**
         * Move the bytes we want into a new list.  This is so we can be sure that a misbehaving opcode doesn't try to
         * read past the end of the script.  NOTE: Technically we only support scripts up to 2GB!
         */
        scriptBytes = pullBytes((int) lengthInBytes, "script, " + lengthInBytes + " byte(s) to process");

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
            return;
        }

        try {
            int position = 0;

            while (byteStream.available() > 0) {
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
                if(OpCodeSeparator.class.isAssignableFrom(currentWord.getClass())) {
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

    private WordFactory getWordFactory() throws IllegalAccessException, InstantiationException {
        if (wordFactory == null) {
            wordFactory = new WordFactory();
        }

        return wordFactory;
    }

    public List<Word> getWords() {
        return words;
    }

    public int getVersionNumber() {
        return versionNumber;
    }
}
