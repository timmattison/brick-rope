package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.factories.ScriptFactory;
import com.timmattison.cryptocurrency.standard.OutputScript;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_output_of_a_transaction_-_Txout
 */
public class BitcoinOutput implements Output {
    private static final int valueLengthInBytes = 8;
    private static final BigDecimal satoshiPerBitcoin = new BigDecimal(100000000);
    private final ScriptFactory scriptFactory;

    /**
     * Value
     */
    private long value;
    private byte[] valueBytes;

    /**
     * Output script length
     */
    private long outputScriptLength;
    private byte[] outputScriptLengthBytes;

    /**
     * Output script
     */
    private OutputScript outputScript;

    // These values are not in the output
    private final int transactionVersionNumber;
    private final int outputNumber;
    private OutputScript script;

    public BitcoinOutput(ScriptFactory scriptFactory, int transactionVersionNumber, int outputNumber) {
        this.scriptFactory = scriptFactory;
        this.transactionVersionNumber = transactionVersionNumber;
        this.outputNumber = outputNumber;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the value
        valueBytes = Arrays.copyOfRange(data, 0, valueLengthInBytes);
        data = Arrays.copyOfRange(data, valueLengthInBytes, data.length);
        value = EndiannessHelper.BytesToLong(valueBytes);

        // Get the output script length
        VariableLengthInteger temp = new VariableLengthInteger();
        data = temp.build(data);
        outputScriptLengthBytes = temp.getValueBytes();
        outputScriptLength = temp.getValue();

        // Get the output script
        outputScript = scriptFactory.createOutputScript(transactionVersionNumber, outputScriptLength);
        return outputScript.build(data);
    }
}
