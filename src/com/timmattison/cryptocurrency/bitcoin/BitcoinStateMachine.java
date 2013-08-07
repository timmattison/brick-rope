package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.WordFactory;
import com.timmattison.cryptocurrency.standard.Script;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinStateMachine implements StateMachine {
    private static final int MAX_WORD_LIST_LENGTH = 9999;
    private final WordFactory wordFactory;
    Stack<Object> stack;

    @Inject
    public BitcoinStateMachine(WordFactory wordFactory) {
        this.wordFactory = wordFactory;
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
        byte[] scriptData = Arrays.copyOf(script.dump(), script.dump().length);
        int wordCounter = 0;

        reset();

        if (!script.isExecutable()) {
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
            Word currentWord = wordFactory.createWord(currentByte);
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

    private void throwExceptionIfStackNotInitialized() {
        if (stack == null) {
            throw new IllegalStateException("Stack has not been initialzied");
        }
    }
}
