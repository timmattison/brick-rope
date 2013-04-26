package com.timmattison.bitcoin.test.script;

import com.timmattison.bitcoin.test.BlockChain;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:59 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ByteConsumingWord extends Word {
    protected byte[] input;
    protected Object output;

    public ByteConsumingWord(String word, Byte opcode) {
        super(word, opcode);
    }

    public void consumeInput(ByteArrayInputStream input) {
        boolean innerDebug = false;

        // Is there any input?
        if (input == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        int inputBytesRequired = getInputBytesRequired();
        if(innerDebug) {
            System.out.println("Input bytes required: " + inputBytesRequired);
        }

        int inputSize = input.available();

        // Do we have enough bytes to do this successfully?
        if (inputSize < inputBytesRequired) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input needs " + inputBytesRequired + " byte(s) but only has " + inputSize + " byte(s)");
        }

        // Get the bytes we need
        this.input = new byte[inputBytesRequired];
        input.read(this.input, 0, inputBytesRequired);
        if(innerDebug) { System.out.println("Input bytes read: " + inputBytesRequired); }

        // Is there additional processing that needs to be done?
        if (isAdditionalProcessingRequired()) {
            // Yes, let the word do any additional processing it needs to
            if(innerDebug) { System.out.println("About to do additional processing"); }
            doAdditionalProcessing(input);
        }
    }

    protected void validateFirstStageInput() {
        // Do we have the data we need?
        if ((input == null) || (input.length != getInputBytesRequired())) {
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

    protected abstract void doAdditionalProcessing(ByteArrayInputStream input);

    protected abstract boolean isAdditionalProcessingRequired();

    public abstract int getInputBytesRequired();

    public abstract void execute(StateMachine stateMachine);
}
