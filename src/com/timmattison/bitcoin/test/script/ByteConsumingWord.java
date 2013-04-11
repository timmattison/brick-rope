package com.timmattison.bitcoin.test.script;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ByteConsumingWord extends Word {
    protected List<Byte> input;
    protected Object output;

    public ByteConsumingWord(String word, int opcode) {
        super(word, opcode);
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

    public abstract void execute(StateMachine stateMachine);
}
