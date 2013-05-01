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

    private boolean coinbase;
    private int versionNumber;

    // Raw bytes, in order they were pulled from the block chain

    /**
     * Previous output index bytes
     */
    private byte[] previousOutputIndexBytes;

    /**
     * Input script length bytes
     */
    private byte[] inputScriptLengthBytes;

    /**
     * Sequence number bytes
     */
    private byte[] sequenceNumberBytes;

    public Input(InputStream inputStream, boolean coinbase, int versionNumber, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);

        this.coinbase = coinbase;
        this.versionNumber = versionNumber;
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
        previousOutputIndexBytes = pullBytes(previousOutputIndexLengthInBytes, "input, previous output index");
        previousOutputIndex = EndiannessHelper.BytesToInt(previousOutputIndexBytes);
        if(isInnerDebug()) { getLogger().info("Previous output index: " + previousOutputIndex); }

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        inputScriptLengthBytes = temp.getBytes();
        inputScriptLength = temp.getValue();
        if(isInnerDebug()) { getLogger().info("Input script length: " + inputScriptLength); }

        try {
            // Get the input script
            inputScript = new Script(inputStream, inputScriptLength, this.coinbase, this.versionNumber, isDebug(), isInnerDebug());
            inputScript.build();
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        // Get the sequence number
        sequenceNumberBytes = pullBytes(sequenceNumberLengthInBytes, "input, sequence number");
        sequenceNumber = EndiannessHelper.BytesToInt(sequenceNumberBytes);
        if(isInnerDebug()) { getLogger().info("Sequence number: " + sequenceNumber); }
    }
}
