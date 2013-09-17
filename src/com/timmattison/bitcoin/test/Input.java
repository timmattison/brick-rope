package com.timmattison.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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

    // These are not part of the script
    private boolean coinbase;
    private int versionNumber;
    private int inputNumber;

    /**
     * Previous transaction hash
     */
    private byte[] previousTransactionHash;

    /**
     * Previous output index
     */
    private long previousOutputIndex;
    private byte[] previousOutputIndexBytes;

    /**
     * Input script length
     */
    private long inputScriptLength;
    private byte[] inputScriptLengthBytes;

    /**
     * Input script
     */
    private Script inputScript;

    /**
     * Sequence number
     */
    private long sequenceNumber;
    private byte[] sequenceNumberBytes;

    public Input(InputStream inputStream, boolean coinbase, int versionNumber, int inputNumber, boolean debug) throws IOException {
        super(inputStream, debug);

        this.coinbase = coinbase;
        this.versionNumber = versionNumber;
        this.inputNumber = inputNumber;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void build() throws IOException {
        // Get the previous transaction hash
        previousTransactionHash = pullBytes(previousTransactionHashLengthInBytes, "input, previous transaction hash");

        // Get the previous output index
        previousOutputIndexBytes = pullBytes(previousOutputIndexLengthInBytes, "input, previous output index");
        previousOutputIndex = EndiannessHelper.BytesToInt(previousOutputIndexBytes);

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        inputScriptLengthBytes = temp.getValueBytes();
        inputScriptLength = temp.getValue();

        try {
            // Get the input script
            inputScript = new Script(inputStream, inputScriptLength, coinbase, versionNumber, inputNumber, isDebug());
            inputScript.build();
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }

        // Get the sequence number
        sequenceNumberBytes = pullBytes(sequenceNumberLengthInBytes, "input, sequence number");
        sequenceNumber = EndiannessHelper.BytesToInt(sequenceNumberBytes);
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Input data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tPrevious transaction hash: ", "\n", previousTransactionHash);
        DumpHelper.dump(stringBuilder, pretty, "\tPrevious output index: ", "\n", previousOutputIndexBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tInput script length: ", "\n", inputScriptLengthBytes);

        stringBuilder.append(inputScript.dump(pretty));

        DumpHelper.dump(stringBuilder, pretty, "\tSequence number: ", "\n", sequenceNumberBytes);

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bytes.write(previousTransactionHash);
        bytes.write(previousOutputIndexBytes);
        bytes.write(inputScriptLengthBytes);
        bytes.write(inputScript.dumpBytes());
        bytes.write(sequenceNumberBytes);

        return bytes.toByteArray();
    }

    public byte[] getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public long getPreviousOutputIndex() {
        return previousOutputIndex;
    }

    public Script getScript() {
        return inputScript;
    }

    public void setScript(byte[] scriptBytes) throws IllegalAccessException, IOException, InstantiationException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(scriptBytes);
        inputScript = new Script(byteArrayInputStream, scriptBytes.length, false, -1, -1, false);
        inputScript.build();
    }
}
