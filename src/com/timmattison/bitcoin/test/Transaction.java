package com.timmattison.bitcoin.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
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

    // These are not in the block chain
    private int transactionCounter;
    private byte[] hash;

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
            Input input = new Input(inputStream, coinbase, versionNumber, inputLoop, isDebug());
            input.build();
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger(inputStream, isDebug());
        outCounterBytes = temp.getValueBytes();
        outCounter = temp.getValue();

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = new Output(inputStream, outputLoop, isDebug());
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
            stringBuilder.append("Transaction #");
            stringBuilder.append(transactionCounter);
            stringBuilder.append(" data:\n");
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

        DumpHelper.dump(stringBuilder, pretty, "\tLock time: ", "\n", lockTimeBytes);

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bytes.write(versionNumberBytes);
        bytes.write(inCounterBytes);

        for(Input input : inputs) {
            bytes.write(input.dumpBytes());
        }

        bytes.write(outCounterBytes);

        for(Output output : outputs) {
            bytes.write(output.dumpBytes());
        }

        bytes.write(lockTimeBytes);

        return bytes.toByteArray();
    }

    public Output getOutput(int outputNumber) {
        if(outputs == null) {
            throw new UnsupportedOperationException("Outputs are NULL, has this transaction been populated?");
        }

        if(outputs.size() < (outputNumber - 1)) {
            throw new UnsupportedOperationException("Not enough outputs, has this transaction been populated?");
        }

        // Get the output
        return outputs.get(outputNumber);
    }

    public Input getInput(int inputNumber) {
        if(inputs == null) {
            throw new UnsupportedOperationException("No inputs, has this transaction been populated?");
        }

        if(inputs.size() < (inputNumber - 1)) {
            throw new UnsupportedOperationException("No inputs, has this transaction been populated?");
        }

        return inputs.get(inputNumber);
    }

    public List<Output> getOutputs() {
        return outputs;
    }

    public byte[] getHash() throws IOException, NoSuchAlgorithmException {
        if(hash == null) {
            hash = HashHelper.doubleSha256Hash(dumpBytes());
        }

        return hash;
    }
}
