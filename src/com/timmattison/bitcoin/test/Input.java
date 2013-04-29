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

    public Input(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
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
        // Get the previous transaction hash
        previousTransactionHash = pullBytes(previousTransactionHashLengthInBytes, "input, previous transaction hash");
        if(isInnerDebug()) { getLogger().info("Previous transaction hash: " + ByteArrayHelper.formatArray(previousTransactionHash)); }

        // Get the previous output index
        previousOutputIndex = EndiannessHelper.BytesToInt(pullBytes(previousOutputIndexLengthInBytes, "input, previous output index"));
        if(isInnerDebug()) { getLogger().info("Previous output index: " + previousOutputIndex); }

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        inputScriptLength = temp.getValue();
        if(isInnerDebug()) { getLogger().info("Input script length: " + inputScriptLength); }

        try {
            // Get the input script
            inputScript = new Script(inputScriptLength, inputStream, isDebug(), isInnerDebug());
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        // Get the sequence number
        sequenceNumber = EndiannessHelper.BytesToInt(pullBytes(sequenceNumberLengthInBytes, "input, sequence number"));
        if(isInnerDebug()) { getLogger().info("Sequence number: " + sequenceNumber); }
    }
}
