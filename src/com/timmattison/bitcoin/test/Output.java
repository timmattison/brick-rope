package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions#general_format_.28inside_a_block.29_of_each_output_of_a_transaction_-_Txout
 */
public class Output extends ByteConsumer {
    private static final String name = "OUTPUT";

    private static final int valueLengthInBytes = 8;
    private static final BigDecimal satoshiPerBitcoin = new BigDecimal(100000000);

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
    private Script outputScript;

    public Output(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void build() throws IOException {
        // Get the value
        valueBytes = pullBytes(valueLengthInBytes, "output, value");
        value = EndiannessHelper.BytesToLong(valueBytes);

        // Get the output script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        outputScriptLengthBytes = temp.getValueBytes();
        outputScriptLength = temp.getValue();

        try {
            // Get the input script
            outputScript = new Script(inputStream, outputScriptLength, isDebug());
            outputScript.build();
        } catch (IllegalAccessException e) {
            throw new UnsupportedOperationException(e);
        } catch (InstantiationException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String getDecimalValue() {
        BigDecimal returnValue = new BigDecimal(value);
        returnValue = returnValue.divide(satoshiPerBitcoin);
        return returnValue.toString();
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Output data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tValue: ", "\n", valueBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tOutput script length: ", "\n", outputScriptLengthBytes);

        stringBuilder.append(outputScript.dump(pretty));

        return stringBuilder.toString();
    }
}
