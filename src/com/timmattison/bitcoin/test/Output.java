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

    private long value;
    private long outputScriptLength;
    private Script outputScript;

    public Output(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
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
        getLogger().info("Value: " + getDecimalValue() + " BTC [" + value + "]");
        getLogger().info("Output script length: " + outputScriptLength);
        outputScript.showDebugInfo();
    }

    @Override
    protected void build() throws IOException {
        // Get the value
        value = EndiannessHelper.BytesToLong(pullBytes(valueLengthInBytes, "output, value"));

        // Get the output script length
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        outputScriptLength = temp.getValue();
        if(isInnerDebug()) { getLogger().info("output, output script length: " + outputScriptLength); }

        try {
            // Get the input script
            outputScript = new Script(outputScriptLength, inputStream, false, isDebug(), isInnerDebug());
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
}
