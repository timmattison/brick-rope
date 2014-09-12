package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.words.crypto.OpCodeSeparator;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.standard.interfaces.Script;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Stack;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinStateMachine implements StateMachine {
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    private final ScriptingFactory scriptingFactory;
    private final Logger logger;
    Stack<Object> stack;
    private byte[] previousTransactionHash;
    private byte[] currentTransactionHash;
    private Integer previousOutputIndex;
    private Integer inputNumber;

    @Inject
    public BitcoinStateMachine(Logger logger, ScriptingFactory scriptingFactory) {
        this.logger = logger;
        this.scriptingFactory = scriptingFactory;
    }

    public Object pop() {
        throwExceptionIfStackNotInitialized();
        return stack.pop();
    }

    public void push(Object input) {
        if (stack == null) {
            stack = new Stack<Object>();
        }

        stack.push(input);
    }

    public Object peek() {
        throwExceptionIfStackNotInitialized();
        return stack.peek();
    }

    @Override
    public void reset() {
        stack = null;
    }

    @Override
    public void execute(Script script) {
        // Get a copy of the script
        byte[] scriptData = Arrays.copyOf(script.dump(), script.dump().length);

        // Initialize the word counter
        int wordCounter = 0;

        // Reset the state machine
        reset();

        // Is this script executable?
        if (!script.isExecutable()) {
            // No, just return
            return;
        }

        do {
            // Are there too many words?
            if (wordCounter > MAX_WORD_LIST_LENGTH) {
                // Yes, throw an exception
                throw new UnsupportedOperationException("The maximum number of words in a script is " + MAX_WORD_LIST_LENGTH + ", saw " + wordCounter++ + " word(s)");
            }

            // Build the next word
            byte currentByte = scriptData[0];

            // Get the word that the next byte corresponds to
            Word currentWord = scriptingFactory.createWord(currentByte);

            // Build the current word and get the remaining script data
            scriptData = currentWord.build(Arrays.copyOfRange(scriptData, 1, scriptData.length));

            // Execute this word
            currentWord.execute(this);
        } while ((scriptData != null) && (scriptData.length > 0));

        // Pop the top value of the stack
        Object topStackValue = pop();

        // Is the top stack value NULL?
        if (topStackValue == null) {
            // No, failure
            throw new UnsupportedOperationException("Stack is empty, script failed");
        }

        // Is the top stack value an integer
        if (!(topStackValue instanceof Integer)) {
            // No, failure
            throw new UnsupportedOperationException("Top stack value is not an integer, script failed");
        }

        // Is the top stack value 1?
        int intTopStackValue = (Integer) topStackValue;

        if (intTopStackValue != 1) {
            // No, failure
            throw new UnsupportedOperationException("Top stack value is not 1, script failed");
        }

        // Success
        return;
    }

    @Override
    public void setPreviousTransactionHash(byte[] previousTransactionHash) {
        this.previousTransactionHash = previousTransactionHash;
    }

    @Override
    public void setCurrentTransactionHash(byte[] currentTransactionHash) {
        this.currentTransactionHash = currentTransactionHash;
    }

    @Override
    public byte[] getPreviousTransactionHash() {
        if (previousTransactionHash == null) {
            throw new UnsupportedOperationException("Previous transaction hash has not been set");
        }

        return previousTransactionHash;
    }

    @Override
    public byte[] getCurrentTransactionHash() {
        if (currentTransactionHash == null) {
            throw new UnsupportedOperationException("Current transaction hash has not been set");
        }

        return currentTransactionHash;
    }

    @Override
    public void setPreviousOutputIndex(int previousOutputIndex) {
        this.previousOutputIndex = previousOutputIndex;
    }

    @Override
    public int getPreviousOutputIndex() {
        if (previousOutputIndex == null) {
            throw new UnsupportedOperationException("Previous output index has not been set");
        }

        return previousOutputIndex;
    }

    @Override
    public int getInputNumber() {
        if (inputNumber == null) {
            throw new UnsupportedOperationException("Input number has not been set");
        }

        return inputNumber;
    }

    @Override
    public void setInputNumber(int inputNumber) {
        this.inputNumber = inputNumber;
    }

    private void throwExceptionIfStackNotInitialized() {
        if (stack == null) {
            throw new IllegalStateException("Stack has not been initialized");
        }
    }
}
