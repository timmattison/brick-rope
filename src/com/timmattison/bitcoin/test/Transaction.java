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
    private static final long currentVersionNumber = 1;
    private static final int versionNumberLengthInBytes = 4;
    private static final int lockTimeLengthInBytes = 4;
    private int versionNumber;
    private long inCounter;
    private List<Input> inputs;
    private long outCounter;
    private List<Output> outputs;
    private int lockTime;

    public Transaction(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        if (isDebug()) {
            getLogger().info("Version: " + versionNumber);
            getLogger().info("Input counter: " + inCounter);

            for (Input input : inputs) {
                input.showDebugInfo();
            }

            getLogger().info("Output counter: " + outCounter);

            for (Output output : outputs) {
                output.showDebugInfo();
            }

            getLogger().info("Lock time: " + lockTime);
        }
    }

    @Override
    protected void build() throws IOException {
        // Get the version number
        versionNumber = EndiannessHelper.BytesToInt(pullBytes(versionNumberLengthInBytes, "transaction, version number"));

        // Sanity check the version number
        if (versionNumber != currentVersionNumber) {
            throw new UnsupportedOperationException("Expected version number is " + currentVersionNumber + ", saw " + versionNumber);
        }

        // Get the input counter
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        inCounter = temp.getValue();
        if(isInnerDebug()) { getLogger().info("transaction, in counter: " + inCounter); }

        // Get the inputs
        for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
            // Input 0 is the coinbase, all other inputs are not
            boolean coinbase = (inputLoop == 0) ? true : false;
            Input input = new Input(inputStream, coinbase, isDebug(), isInnerDebug());
            input.build();
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        outCounter = temp.getValue();
        if(isInnerDebug()) { getLogger().info("transaction, out counter: " + outCounter); }

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = new Output(inputStream, isDebug(), isInnerDebug());
            output.build();
            addOutput(output);
        }

        // Get the lock time
        lockTime = EndiannessHelper.BytesToInt(pullBytes(lockTimeLengthInBytes, "transaction, lock time"));
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
}
