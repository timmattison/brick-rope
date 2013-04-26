package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:46 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_input_of_a_transaction_-_Txin
 */
public class Input extends ByteConsumer {
    private static final String name = "INPUT";

    private static final int previousTransactionHashLengthInBytes = 32;
    private static final int previousOutputIndexLengthInBytes = 4;
    private static final int sequenceNumberLengthInBytes = 4;

    private byte[] previousTransactionHash;
    private long previousOutputIndex;
    private Script inputScript;
    private long sequenceNumber;

    // This is not part of the script
    private long inputScriptLength;

    public Input(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);
    }

    @Override
    protected void initialize(Object[] objects) {
        throw new UnsupportedOperationException("Additional initialization not necessary");
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        getLogger().info("Previous transaction hash: " + ByteArrayHelper.formatArray(previousTransactionHash));
        getLogger().info("Previous output index: " + previousOutputIndex);
        inputScript.showDebugInfo();
        getLogger().info("Sequence number: " + sequenceNumber);
    }

    @Override
    protected void build() throws IOException {
        boolean innerDebug = false;

        if(BlockChain.blockNumber > 29650) {
            innerDebug = true;
        }

        // Get the previous transaction hash
        previousTransactionHash = pullBytes(previousTransactionHashLengthInBytes);
        if(innerDebug) { getLogger().info("Previous transaction hash: " + ByteArrayHelper.formatArray(previousTransactionHash)); }

        // Get the previous output index
        previousOutputIndex = EndiannessHelper.BytesToInt(pullBytes(previousOutputIndexLengthInBytes));
        if(innerDebug) { getLogger().info("Previous output index: " + previousOutputIndex); }

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        inputScriptLength = temp.getValue();
        if(innerDebug) { getLogger().info("Input script length: " + inputScriptLength); }

        try {
            // Get the input script
            inputScript = new Script(inputScriptLength, inputStream, isDebug());
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        // Get the sequence number
        sequenceNumber = EndiannessHelper.BytesToInt(pullBytes(sequenceNumberLengthInBytes));
        if(innerDebug) { getLogger().info("Sequence number: " + sequenceNumber); }
    }
}
