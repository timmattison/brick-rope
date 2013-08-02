package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
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
    private final byte[] data;
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

    public BitcoinTransaction(byte[] data, InputFactory inputFactory, OutputFactory outputFactory, long maxVersionNumber, int transactionCounter) {
        this.data = data;
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.maxVersionNumber = maxVersionNumber;
        this.transactionCounter = transactionCounter;
    }

    // TEMP: This is the current max version number in the Bitcoin block chain right now
    public BitcoinTransaction(byte[] data, InputFactory inputFactory, OutputFactory outputFactory, int transactionCounter) {
        this.data = data;
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.maxVersionNumber = 2;
        this.transactionCounter = transactionCounter;
    }

    @Override
    public Iterator<Input> getInputIterator() {
        return inputs.iterator();
    }

    @Override
    public Iterator<Output> getOutputIterator() {
        return outputs.iterator();
    }

    @Override
    public void build() {
        try {
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
            VariableLengthInteger temp = new VariableLengthInteger(inputStream);
            inCounterBytes = temp.getValueBytes();
            inCounter = temp.getValue();

            // Get the inputs
            for (int inputLoop = 0; inputLoop < inCounter; inputLoop++) {
                // Input 0 is the coinbase, all other inputs are not
                boolean coinbase = ((transactionCounter == 0) && (inputLoop == 0)) ? true : false;
                Input input = inputFactory.createInput(inputStream, coinbase, inputLoop);
                input.build();
                addInput(input);
            }

            // Get the output counter
            temp = new VariableLengthInteger(inputStream);
            outCounterBytes = temp.getValueBytes();
            outCounter = temp.getValue();

            // Get the outputs
            for (int outputLoop = 0; outputLoop < outCounter; outputLoop++) {
                Output output = outputFactory.createOutput(inputStream, outputLoop);
                output.build();
                addOutput(output);
            }

            // Get the lock time
            lockTimeBytes = InputStreamHelper.pullBytes(inputStream, lockTimeLengthInBytes);
            lockTime = EndiannessHelper.BytesToInt(lockTimeBytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private void addInput(Input input) {
        if(inputs == null) {
            inputs = new ArrayList<Input>();
        }

        inputs.add(input);
    }

    private void addOutput(Output output) {
        if(outputs == null) {
            outputs = new ArrayList<Output>();
        }

        outputs.add(output);
    }
}
