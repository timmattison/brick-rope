package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.VariableLengthInteger;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 6:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTransaction implements Transaction {
    private static final Logger logger = Logger.getLogger(BitcoinTransaction.class.getName());

    private static final int versionNumberLengthInBytes = 4;
    private static final int lockTimeLengthInBytes = 4;
    private final long maxVersionNumber = 2;
    private final int transactionCounter;
    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final HasherFactory hasherFactory;
    private byte[] hashBytes;
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

    @Inject
    public BitcoinTransaction(InputFactory inputFactory, OutputFactory outputFactory, HasherFactory hasherFactory, int transactionCounter) {
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.hasherFactory = hasherFactory;
        this.transactionCounter = transactionCounter;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the version number
        versionNumberBytes = Arrays.copyOfRange(data, 0, versionNumberLengthInBytes);
        data = Arrays.copyOfRange(data, versionNumberLengthInBytes, data.length);
        versionNumber = EndiannessHelper.BytesToInt(versionNumberBytes);

        // Sanity check the version number
        if (versionNumber > maxVersionNumber) {
            throw new UnsupportedOperationException("Max version number is " + maxVersionNumber + ", saw " + versionNumber);
        }

        if (versionNumber <= 0) {
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
            Input input = inputFactory.createInput(versionNumber, inputLoop);
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
            Output output = outputFactory.createOutput(versionNumber, outputLoop);
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

    @Override
    public byte[] dump() {
        try {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            //logger.fine("Version number bytes: " + ByteArrayHelper.toHex(versionNumberBytes));
            bytes.write(versionNumberBytes);
            //logger.fine("In counter bytes: " + ByteArrayHelper.toHex(inCounterBytes));
            bytes.write(inCounterBytes);

            for (Input input : inputs) {
                //logger.fine("Input: " + ByteArrayHelper.toHex(input.dump()));
                bytes.write(input.dump());
            }

            //logger.fine("Out counter bytes: " + ByteArrayHelper.toHex(outCounterBytes));
            bytes.write(outCounterBytes);

            for (Output output : outputs) {
                //logger.fine("Output: " + ByteArrayHelper.toHex(output.dump()));
                bytes.write(output.dump());
            }

            //logger.fine("Lock time bytes: " + ByteArrayHelper.toHex(lockTimeBytes));
            bytes.write(lockTimeBytes);

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
        stringBuilder.append("Version number: ");
        stringBuilder.append(ByteArrayHelper.toHex(versionNumberBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Input counter: ");
        stringBuilder.append(ByteArrayHelper.toHex(inCounterBytes));
        stringBuilder.append("\n");

        for (int loop = 0; loop < inputs.size(); loop++) {
            stringBuilder.append(indentation);
            stringBuilder.append("Input script #");
            stringBuilder.append(loop);
            stringBuilder.append(": ");
            stringBuilder.append(inputs.get(loop).prettyDump(indentationLevel + 1));
            stringBuilder.append("\n");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Output counter: ");
        stringBuilder.append(ByteArrayHelper.toHex(outCounterBytes));
        stringBuilder.append("\n");

        for (int loop = 0; loop < outputs.size(); loop++) {
            stringBuilder.append(indentation);
            stringBuilder.append("Output script #");
            stringBuilder.append(loop);
            stringBuilder.append(": ");
            stringBuilder.append(outputs.get(loop).prettyDump(indentationLevel + 1));
            stringBuilder.append("\n");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Lock time: ");
        stringBuilder.append(ByteArrayHelper.toHex(lockTimeBytes));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    @Override
    public byte[] getHash() {
        if (hashBytes == null) {
            hashBytes = hasherFactory.createHasher(dump()).getOutput();
        }

        return hashBytes;
    }
}
