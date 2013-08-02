package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.interfaces.ScriptFactory;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

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
public class BitcoinInput implements Input {
    private static final int previousTransactionHashLengthInBytes = 32;
    private static final int previousOutputIndexLengthInBytes = 4;
    private static final int sequenceNumberLengthInBytes = 4;
    private final InputStream inputStream;
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

    public BitcoinInput(InputStream inputStream, ScriptFactory scriptFactory, boolean coinbase, int inputNumber) {
        this.inputStream = inputStream;
        this.scriptFactory = scriptFactory;
        this.coinbase = coinbase;
        this.inputNumber = inputNumber;
    }

    @Override
    public void build() {
        try {
            // Get the previous transaction hash
            previousTransactionHash = InputStreamHelper.pullBytes(inputStream, previousTransactionHashLengthInBytes);

            // Get the previous output index
            previousOutputIndexBytes = InputStreamHelper.pullBytes(inputStream, previousOutputIndexLengthInBytes);
            previousOutputIndex = EndiannessHelper.BytesToInt(previousOutputIndexBytes);

            // Get the input script length
            VariableLengthInteger temp = new VariableLengthInteger(inputStream);
            temp.build();
            inputScriptLengthBytes = temp.getValueBytes();
            inputScriptLength = temp.getValue();

            // Get the input script
            inputScript = scriptFactory.createInputScript(inputStream, inputScriptLength, coinbase);
            inputScript.build();

            sequenceNumber = EndiannessHelper.BytesToInt(sequenceNumberBytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public byte[] getPreviousTransactionHash() {
        return previousTransactionHash;
    }

    public long getPreviousOutputIndex() {
        return previousOutputIndex;
    }
}
