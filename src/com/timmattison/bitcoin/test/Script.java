package com.timmattison.bitcoin.test;

import com.timmattison.bitcoin.test.script.ByteConsumingWord;
import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

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
    private static final int MAX_WORD_LIST_LENGTH = 201;
    private long lengthInBytes;
    private List<Word> words;
    private StateMachine stateMachine;
    private WordFactory wordFactory;

    public Script(long lengthInBytes, InputStream inputStream, boolean debug) throws IllegalAccessException, InstantiationException, IOException {
        super(inputStream, debug, new Object[]{lengthInBytes});
    }

    public Script(long lengthInBytes, Byte[] bytes, boolean debug) throws IllegalAccessException, InstantiationException, IOException {
        super(bytes, debug, new Object[]{lengthInBytes});
    }

    @Override
    protected void initialize(Object[] objects) {
        this.lengthInBytes = (Long) objects[0];
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        if (isDebug()) {
            getLogger().info(getListOfWords());
        }
    }

    public String getListOfWords() {
        StringBuilder stringBuilder = new StringBuilder();

        for (Word word : words) {
            stringBuilder.append(word.getWord());
            stringBuilder.append("\n");
        }

        return stringBuilder.toString();
    }

    public Object execute() {
        for (Word word : words) {
            word.execute(getStateMachine());
        }

        return getStateMachine().stack.pop();
    }

    private StateMachine getStateMachine() {
        if (stateMachine == null) {
            stateMachine = new StateMachine();
        }

        return stateMachine;
    }

    @Override
    protected void build() throws IOException {
        boolean innerDebug = false;

        words = new ArrayList<Word>();

        // Is there any data?
        if ((inputStream == null) || (inputStream.available() == 0)) {
            // No, throw an exception
            throw new UnsupportedOperationException("The script is empty");
        }

        // Are there enough bytes to support that?
        if (inputStream.available() < lengthInBytes) {
            // No, throw an exception
            throw new UnsupportedOperationException("Script is supposed to have " + lengthInBytes + " byte(s) but only " + inputStream.available() + " byte(s) are left");
        }

        /**
         * Move the bytes we want into a new list.  This is so we can be sure that a misbehaving opcode doesn't try to
         * read past the end of the script.  NOTE: Technically we only support scripts up to 2GB!
         */
        byte[] bytesToProcess = pullBytes((int) lengthInBytes, "script, bytes to process");

        ByteArrayInputStream byteStream = new ByteArrayInputStream(bytesToProcess);

        try {
            while (byteStream.available() > 0) {
                // Get the next byte
                byte currentByte = (byte) byteStream.read();
                if(innerDebug) { getLogger().info("Current byte: " + currentByte); }

                // Get the word that the next byte corresponds to
                Word currentWord = getWordFactory().getWordByOpcode(currentByte);
                if(innerDebug) { getLogger().info("Current word: " + currentWord.getWord()); }

                // Is this a byte consuming word?
                if (ByteConsumingWord.class.isAssignableFrom(currentWord.getClass())) {
                    if(innerDebug) { getLogger().info("Word about to consume some bytes"); }
                    // Yes, consume the bytes
                    ((ByteConsumingWord) currentWord).consumeInput(byteStream);
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

    private WordFactory getWordFactory() throws IllegalAccessException, InstantiationException {
        if (wordFactory == null) {
            wordFactory = new WordFactory();
        }

        return wordFactory;
    }
}
