package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 6:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTransaction implements Transaction {
    private static final int versionNumberLengthInBytes = 4;
    private static final int lockTimeLengthInBytes = 4;
    private final long maxVersionNumber = 1;
    private final int transactionCounter;
    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;

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

    public BitcoinTransaction(InputFactory inputFactory, OutputFactory outputFactory, int transactionCounter) {
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.transactionCounter = transactionCounter;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the version number
        versionNumberBytes = Arrays.copyOfRange(data, 0, versionNumberLengthInBytes);
        versionNumber = EndiannessHelper.BytesToInt(versionNumberBytes);

        // Sanity check the version number
        if (versionNumber > maxVersionNumber) {
            throw new UnsupportedOperationException("Max version number is " + maxVersionNumber + ", saw " + versionNumber);
        }

        if(versionNumber <= 0) {
            throw new UnsupportedOperationException("Version number cannot be less than or equal to zero");
        }

        // Get the input counter
        VariableLengthInteger temp = new VariableLengthInteger();
        data = temp.build(data);
        inCounterBytes = temp.getValueBytes();
        inCounter = temp.getValue();

        // Get the inputs
        for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
            // Input 0 is the coinbase, all other inputs are not
            Input input = inputFactory.createInput(inputLoop);
            data = input.build(data);
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger();
        data = temp.build(data);
        outCounterBytes = temp.getValueBytes();
        outCounter = temp.getValue();

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = outputFactory.createOutput(outputLoop);
            data = output.build(data);
            addOutput(output);
        }

        // Get the lock time
        lockTimeBytes = Arrays.copyOfRange(data, 0, lockTimeLengthInBytes);
        lockTime = EndiannessHelper.BytesToInt(lockTimeBytes);

        return Arrays.copyOfRange(data, lockTimeLengthInBytes, data.length);
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
    public List<Input> getInputs() {
        return new ArrayList<Input>(inputs);
    }

    @Override
    public List<Output> getOutputs() {
        return new ArrayList<Output>(outputs);
    }
}
