package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.factories.ScriptFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:46 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_input_of_a_transaction_-_Txin
 */
public class BitcoinInput implements Input {
    private static final int previousTransactionHashLengthInBytes = 32;
    private static final int previousOutputIndexLengthInBytes = 4;
    private static final int sequenceNumberLengthInBytes = 4;
    private final ScriptFactory scriptFactory;
    // These are not part of the script
    private final int transactionVersionNumber;
    private final int inputNumber;
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
    private InputScript inputScript;
    /**
     * Sequence number
     */
    private long sequenceNumber;
    private byte[] sequenceNumberBytes;

    public BitcoinInput(ScriptFactory scriptFactory, int transactionVersionNumber, int inputNumber) {
        this.scriptFactory = scriptFactory;
        this.transactionVersionNumber = transactionVersionNumber;
        this.inputNumber = inputNumber;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the previous transaction hash
        previousTransactionHash = Arrays.copyOfRange(data, 0, previousTransactionHashLengthInBytes);
        data = Arrays.copyOfRange(data, previousTransactionHashLengthInBytes, data.length);

        // Get the previous output index
        previousOutputIndexBytes = Arrays.copyOfRange(data, 0, previousOutputIndexLengthInBytes);
        data = Arrays.copyOfRange(data, previousOutputIndexLengthInBytes, data.length);
        previousOutputIndex = EndiannessHelper.BytesToInt(previousOutputIndexBytes);

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger();
        data = temp.build(data);
        inputScriptLengthBytes = temp.getValueBytes();
        inputScriptLength = temp.getValue();

        // Get the input script
        inputScript = scriptFactory.createInputScript(transactionVersionNumber, inputScriptLength, isCoinbase());
        data = inputScript.build(data);

        // Get the sequence number
        sequenceNumberBytes = Arrays.copyOfRange(data, 0, sequenceNumberLengthInBytes);
        data = Arrays.copyOfRange(data, sequenceNumberLengthInBytes, data.length);
        sequenceNumber = EndiannessHelper.BytesToInt(sequenceNumberBytes);

        return data;
    }

    @Override
    public byte[] dump() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bytes.write(previousTransactionHash);
            bytes.write(previousOutputIndexBytes);
            bytes.write(inputScriptLengthBytes);
            bytes.write(inputScript.dump());
            bytes.write(sequenceNumberBytes);

            return bytes.toByteArray();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public String prettyDump(int indentationLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        StringBuilder indentation = new StringBuilder();

        for (int loop = 0; loop < indentationLevel; loop++) {
            indentation.append("\t");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Previous transaction hash: ");
        stringBuilder.append(ByteArrayHelper.toHex(previousTransactionHash));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Previous output index: ");
        stringBuilder.append(ByteArrayHelper.toHex(previousOutputIndexBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Input script length: ");
        stringBuilder.append(ByteArrayHelper.toHex(inputScriptLengthBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Input script: ");
        stringBuilder.append(inputScript.prettyDump(indentationLevel + 1));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Sequence number: ");
        stringBuilder.append(ByteArrayHelper.toHex(sequenceNumberBytes));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    @Override
    public boolean isCoinbase() {
        return (inputNumber == 0);
    }

    @Override
    public Script getScript() {
        return inputScript;
    }

    @Override
    public byte[] getPreviousTransactionId() {
        return previousTransactionHash;
    }

    @Override
    public long getPreviousOutputIndex() {
        return previousOutputIndex;
    }
}
