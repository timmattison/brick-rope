package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 * <p/>
 * This information was pulled from https://en.bitcoin.it/wiki/Block
 */
public class Block extends ByteConsumer {
    private static final String name = "BLOCK";
    // Used for sanity check
    private static final byte[] requiredMagicNumberBytes = new byte[]{(byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9};
    private static final int requiredMagicNumber = EndiannessHelper.BytesToInt(requiredMagicNumberBytes);
    private static final int magicNumberLengthInBytes = 4;
    private static final int blockSizeLengthInBytes = 4;
    private int magicNumber;
    private int blockSize;
    private BlockHeader blockHeader;
    private int transactionCount;
    private List<Transaction> transactions;

    public Block(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        // Show the block info
        getLogger().info("  Magic number: " + magicNumber);
        getLogger().info("  Block size: " + magicNumber);

        // Show the block header info
        blockHeader.showDebugInfo();

        // Show the info for the transactions
        for (Transaction transaction : transactions) {
            transaction.showDebugInfo();
        }
    }

    @Override
    protected void build() throws IOException {
        if(isDebug()) { getLogger().info("Input stream available: " + inputStream.available()); }

        // Get the magic number and remove the bytes it occupied
        magicNumber = EndiannessHelper.BytesToInt(pullBytes(magicNumberLengthInBytes, "block, magic number"));

        // Validate that the magic number matches the spec
        if (magicNumber != requiredMagicNumber) {
            throw new UnsupportedOperationException("Expected " + requiredMagicNumber + " for the magic number, saw " + magicNumber);
        }

        // Get the block size and remove the bytes it occupied
        blockSize = EndiannessHelper.BytesToInt(pullBytes(blockSizeLengthInBytes, "block, block size"));

        // Sanity check the block size
        if (blockSize <= 0) {
            // This should never happen unless blocks get larger than 2 GB and we're wrapping around
            throw new UnsupportedOperationException("The block size is less than or equal to 0, saw " + blockSize);
        }

        // Get the block header and remove the bytes it occupied
        blockHeader = new BlockHeader(inputStream, isDebug(), isInnerDebug());
        blockHeader.build();

        // Get the transaction count and return the remaining bytes back into the block header byte list
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug(), isInnerDebug());
        transactionCount = (int) temp.getValue();
        if(isInnerDebug()) { getLogger().info("block, transaction count: " + transactionCount); }

        // Sanity check transaction count
        if (transactionCount <= 0) {
            throw new UnsupportedOperationException("Transaction count cannot be less than or equal to 0, saw " + transactionCount);
        }

        // Initialize the transaction list
        transactions = new ArrayList<Transaction>();

        for (int transactionCounter = 0; transactionCounter < transactionCount; transactionCounter++) {
            Transaction transaction = new Transaction(inputStream, isDebug(), isInnerDebug());
            transaction.build();
            transactions.add(transaction);
        }
    }
}
