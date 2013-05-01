package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:42 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Transactions
 */
public class Transaction extends ByteConsumer {
    private static final String name = "TRANSACTION";
    // Used for sanity check
    private static final long maxVersionNumber = 2;
    private static final int versionNumberLengthInBytes = 4;
    private static final int lockTimeLengthInBytes = 4;

    /**
     * Version number
     */
    private int versionNumber;
    private byte[] versionNumberBytes;

    /**
     * Input counter
     */
    private long inCounter;
    private byte[] inCounterBytes;

    /**
     * Inputs
     */
    private List<Input> inputs;

    /**
     * Output counter
     */
    private long outCounter;
    private byte[] outCounterBytes;

    /**
     * Outputs
     */
    private List<Output> outputs;

    /**
     * Lock time
     */
    private int lockTime;
    private byte[] lockTimeBytes;

    private int transactionCounter;

    public Transaction(InputStream inputStream, int transactionCounter, boolean debug) throws IOException {
        super(inputStream, debug);

        this.transactionCounter = transactionCounter;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void build() throws IOException {
        // Get the version number
        versionNumberBytes = pullBytes(versionNumberLengthInBytes, "transaction, version number");
        versionNumber = EndiannessHelper.BytesToInt(versionNumberBytes);

        // Sanity check the version number
        if (versionNumber > maxVersionNumber) {
            throw new UnsupportedOperationException("Max version number is " + maxVersionNumber + ", saw " + versionNumber);
        }

        // Get the input counter
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        inCounterBytes = temp.getValueBytes();
        inCounter = temp.getValue();

        // Get the inputs
        for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
            // Input 0 is the coinbase, all other inputs are not
            boolean coinbase = ((transactionCounter == 0) && (inputLoop == 0)) ? true : false;
            Input input = new Input(inputStream, coinbase, versionNumber, isDebug());
            input.build();
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger(inputStream, isDebug());
        outCounterBytes = temp.getValueBytes();
        outCounter = temp.getValue();

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = new Output(inputStream, isDebug());
            output.build();
            addOutput(output);
        }

        // Get the lock time
        lockTimeBytes = pullBytes(lockTimeLengthInBytes, "transaction, lock time");
        lockTime = EndiannessHelper.BytesToInt(lockTimeBytes);
    }

    private void addInput(Input input) {
        if (inputs == null) {
            inputs = new ArrayList<Input>();
        }

        inputs.add(input);
    }

    private void addOutput(Output output) {
        if (outputs == null) {
            outputs = new ArrayList<Output>();
        }

        outputs.add(output);
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Transaction data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tVersion number: ", "\n", versionNumberBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tInput counter: ", "\n", inCounterBytes);

        for(Input input : inputs) {
            stringBuilder.append(input.dump(pretty));
        }

        DumpHelper.dump(stringBuilder, pretty, "\tOutput counter: ", "\n", outCounterBytes);

        for(Output output : outputs) {
            stringBuilder.append(output.dump(pretty));
        }

        DumpHelper.dump(stringBuilder, pretty, "\tLock time: ", "\n", outCounterBytes);

        return stringBuilder.toString();
    }
}
