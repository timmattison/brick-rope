package com.timmattison.bitcoin.test.script;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class Word {
    protected final int opcode;
    protected final String word;
    protected List<Byte> input;
    protected Object output;

    public Word(String word, int opcode) {
        this.opcode = opcode;
        this.word = word;
    }

    public int getOpcode() {
        return opcode;
    }

    public String getWord() {
        return word;
    }

    public List<Byte> consumeInput(List<Byte> input) {
        // Is there any input?
        if (input == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        int inputBytesRequired = getInputBytesRequired();
        int inputSize = input.size();

        // Do we have enough bytes to do this successfully?
        if (inputSize < inputBytesRequired) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input needs " + inputBytesRequired + " byte(s) but only has " + inputSize + " byte(s)");
        }

        // Get the bytes we need
        this.input = input.subList(0, inputBytesRequired);

        // Remove them from the input
        return input.subList(inputBytesRequired, inputSize);
    }

    public abstract int getInputBytesRequired();

    public Object getOutput() {
        return output;
    }

    public boolean isEnabled() {
        // All words are enabled by default
        return true;
    }

    public abstract void execute(StateMachine stateMachine);
}
