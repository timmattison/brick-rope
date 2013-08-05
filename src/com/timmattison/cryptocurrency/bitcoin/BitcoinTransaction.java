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
    private final long maxVersionNumber;
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

    public BitcoinTransaction(InputFactory inputFactory, OutputFactory outputFactory, long maxVersionNumber, int transactionCounter) {
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.maxVersionNumber = maxVersionNumber;
        this.transactionCounter = transactionCounter;
    }

    @Override
    public byte[] build(byte[] data) {
        int position = 0;

        // Get the version number
        versionNumberBytes = Arrays.copyOfRange(data, position, position + versionNumberLengthInBytes);
        position += versionNumberLengthInBytes;
        versionNumber = EndiannessHelper.BytesToInt(versionNumberBytes);

        // Sanity check the version number
        if (versionNumber > maxVersionNumber) {
            throw new UnsupportedOperationException("Max version number is " + maxVersionNumber + ", saw " + versionNumber);
        }

        // Get the input counter
        VariableLengthInteger temp = new VariableLengthInteger();
        byte[] tempData = temp.build(data);
        inCounterBytes = temp.getValueBytes();
        inCounter = temp.getValue();

        // Get the inputs
        for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
            // Input 0 is the coinbase, all other inputs are not
            boolean coinbase = ((transactionCounter == 0) && (inputLoop == 0)) ? true : false;
            Input input = inputFactory.createInput(coinbase, inputLoop);
            tempData = input.build(tempData);
            addInput(input);
        }

        // Get the output counter
        temp = new VariableLengthInteger();
        tempData = temp.build(tempData);
        outCounterBytes = temp.getValueBytes();
        outCounter = temp.getValue();

        // Get the outputs
        for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
            Output output = outputFactory.createOutput(outputLoop);
            tempData = output.build(tempData);
            addOutput(output);
        }

        // Reset the position since we're working with temporary data
        position = 0;

        // Get the lock time
        lockTimeBytes = Arrays.copyOfRange(data, position, position + lockTimeLengthInBytes);
        position += lockTimeLengthInBytes;
        lockTime = EndiannessHelper.BytesToInt(lockTimeBytes);

        return Arrays.copyOfRange(data, position, data.length);
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
