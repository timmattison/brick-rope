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

    public Transaction(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);
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

    @Override
    protected void build() throws IOException {
        // Get the version number
        versionNumber = EndiannessHelper.BytesToInt(pullBytes(versionNumberLengthInBytes));

        // Sanity check the version number
        if (versionNumber != currentVersionNumber) {
            throw new UnsupportedOperationException("Expected version number is " + currentVersionNumber + ", saw " + versionNumber);
        }

        // Get the input counter
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        inCounter = temp.getValue();

        // Get the inputs
        for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
            Input input = new Input(inputStream, isDebug());
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger(inputStream, isDebug());
        outCounter = temp.getValue();

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = new Output(inputStream, isDebug());
            addOutput(output);
        }

        // Get the lock time
        lockTime = EndiannessHelper.BytesToInt(pullBytes(lockTimeLengthInBytes));
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
