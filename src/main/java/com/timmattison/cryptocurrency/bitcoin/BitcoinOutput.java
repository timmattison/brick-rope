package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.standard.interfaces.OutputScript;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final ScriptingFactory scriptingFactory;
    private final VariableLengthIntegerFactory variableLengthIntegerFactory;
    // These values are not in the output
    private final int transactionVersionNumber;
    private final int outputNumber;
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
    private OutputScript script;

    public BitcoinOutput(ScriptingFactory scriptingFactory, VariableLengthIntegerFactory variableLengthIntegerFactory, int transactionVersionNumber, int outputNumber) {
        this.scriptingFactory = scriptingFactory;
        this.variableLengthIntegerFactory = variableLengthIntegerFactory;
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
        VariableLengthInteger temp = variableLengthIntegerFactory.create();
        data = temp.build(data);
        outputScriptLengthBytes = temp.getValueBytes();
        outputScriptLength = temp.getValue();

        // Get the output script
        outputScript = scriptingFactory.createOutputScript(transactionVersionNumber, outputScriptLength);
        return outputScript.build(data);
    }

    @Override
    public byte[] dump() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            bytes.write(valueBytes);
            bytes.write(outputScriptLengthBytes);

            bytes.write(outputScript.dump());

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
        stringBuilder.append("Value bytes: ");
        stringBuilder.append(ByteArrayHelper.toHex(valueBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Output script length bytes: 0x");
        stringBuilder.append(ByteArrayHelper.toHex(outputScriptLengthBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Output script: ");
        stringBuilder.append(outputScript.prettyDump(indentationLevel + 1));

        return stringBuilder.toString();
    }

    @Override
    public Script getScript() {
        return outputScript;
    }
}
