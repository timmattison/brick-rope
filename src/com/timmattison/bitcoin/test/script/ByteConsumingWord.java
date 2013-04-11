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

    public ByteConsumingWord(String word, Byte opcode) {
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

        // Is there additional processing that needs to be done?
        if (isAdditionalProcessingRequired()) {
            // Yes, let the word do any additional processing it needs to
            input = doAdditionalProcessing(input);
        } else {
            // No, just remove the bytes we consumed from the input
            input = input.subList(inputBytesRequired, inputSize);
        }

        return input;
    }

    protected void validateFirstStageInput() {
        // Do we have the data we need?
        if ((input == null) || (input.size() != getInputBytesRequired())) {
            // No, throw an exception
            throw new UnsupportedOperationException("Didn't get the required number of bytes [" + getInputBytesRequired() + "]");
        }
    }

    protected void validateBytesToRead(int bytesToRead, int inputSize) {
        // Do we have enough data to read the number of bytes they requested?
        if (bytesToRead > inputSize) {
            throw new UnsupportedOperationException("Requested to read " + bytesToRead + " byte(s) but only " + inputSize + " byte(s) are available");
        }
    }

    protected abstract List<Byte> doAdditionalProcessing(List<Byte> input);

    protected abstract boolean isAdditionalProcessingRequired();

    public abstract int getInputBytesRequired();

    public abstract void execute(StateMachine stateMachine);
}
