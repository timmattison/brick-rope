package com.timmattison.bitcoin.test.script;

import com.timmattison.bitcoin.test.BlockChain;
import com.timmattison.bitcoin.test.BlockChainTest;
import com.timmattison.bitcoin.test.ByteArrayHelper;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.logging.Logger;

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
    private Logger logger;

    public ByteConsumingWord(String word, Byte opcode, boolean innerDebug) {
        super(word, opcode, innerDebug);
    }

    public void consumeInput(ByteArrayInputStream input) {
        // Is there any input?
        if (input == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input cannot be NULL");
        }

        int inputBytesRequired = getInputBytesRequired();

        if(isInnerDebug()) { getLogger().info("Input bytes required: " + inputBytesRequired); }

        int inputSize = input.available();

        // Do we have enough bytes to do this successfully?
        if (inputSize < inputBytesRequired) {
            // No, throw an exception
            throw new UnsupportedOperationException("Input needs " + inputBytesRequired + " byte(s) but only has " + inputSize + " byte(s)");
        }

        // Get the bytes we need
        this.input = new byte[inputBytesRequired];
        input.read(this.input, 0, inputBytesRequired);
        if(isInnerDebug()) { getLogger().info("Input bytes read: " + inputBytesRequired + " " + ByteArrayHelper.formatArray(this.input)); }

        // Is there additional processing that needs to be done?
        if (isAdditionalProcessingRequired()) {
            // Yes, let the word do any additional processing it needs to
            if(isInnerDebug()) { getLogger().info("About to do additional processing"); }
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

    protected Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(getWord());

            try {
               logger.addHandler(BlockChainTest.getHandler());
            } catch (Exception ex) {
                // Do nothing, failed to get a handler
            }
        }

        return logger;
    }

    public byte[] getInput() {
        return input;
    }
}
