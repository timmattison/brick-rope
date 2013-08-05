package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.ScriptFactory;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

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
    private boolean coinbase;
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
    private InputScript inputScript;

    /**
     * Sequence number
     */
    private long sequenceNumber;
    private byte[] sequenceNumberBytes;

    public BitcoinInput(ScriptFactory scriptFactory, boolean coinbase, int inputNumber) {
        this.scriptFactory = scriptFactory;
        this.coinbase = coinbase;
        this.inputNumber = inputNumber;
    }

    @Override
    public byte[] build(byte[] data) {
        int position = 0;

        // Get the previous transaction hash
        previousTransactionHash = Arrays.copyOfRange(data, position, position + previousTransactionHashLengthInBytes);
        position += previousTransactionHashLengthInBytes;

        // Get the previous output index
        previousOutputIndexBytes = Arrays.copyOfRange(data, position, position + previousOutputIndexLengthInBytes);
        position += previousOutputIndexLengthInBytes;
        previousOutputIndex = EndiannessHelper.BytesToInt(previousOutputIndexBytes);

        // Get the input script length
        VariableLengthInteger temp = new VariableLengthInteger(data);
        byte[] tempBytes = temp.build();
        inputScriptLengthBytes = temp.getValueBytes();
        inputScriptLength = temp.getValue();

        // Get the input script
        inputScript = scriptFactory.createInputScript(tempBytes, inputScriptLength, coinbase);
        tempBytes = inputScript.build();

        // Start position over as we're working with the temporary array
        position = 0;

        sequenceNumberBytes = Arrays.copyOfRange(tempBytes, position, position + sequenceNumberLengthInBytes);
        position += sequenceNumberLengthInBytes;
        sequenceNumber = EndiannessHelper.BytesToInt(sequenceNumberBytes);

        return Arrays.copyOfRange(tempBytes, position, tempBytes.length);
    }
}
