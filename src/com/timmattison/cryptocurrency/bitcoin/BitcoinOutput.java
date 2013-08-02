package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.interfaces.OutputScript;
import com.timmattison.cryptocurrency.interfaces.ScriptFactory;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;

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
    private final InputStream inputStream;
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
    int outputNumber;
    private Object script;

    public BitcoinOutput(InputStream inputStream, ScriptFactory scriptFactory, int outputNumber) {
        this.inputStream = inputStream;
        this.scriptFactory = scriptFactory;
        this.outputNumber = outputNumber;
    }

    @Override
    public void build() {
        try {
            // Get the value
            valueBytes = InputStreamHelper.pullBytes(inputStream, valueLengthInBytes);
            value = EndiannessHelper.BytesToLong(valueBytes);

            // Get the output script length
            VariableLengthInteger temp = new VariableLengthInteger(inputStream);
            temp.build();
            outputScriptLengthBytes = temp.getValueBytes();
            outputScriptLength = temp.getValue();

            // Get the input script
            outputScript = scriptFactory.createOutputScript(inputStream, outputScriptLength);
            outputScript.build();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
