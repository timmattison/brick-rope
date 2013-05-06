package com.timmattison.bitcoin.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
    // These values are not in the block
    int blockNumber;
    /**
     * Magic number
     */
    private int magicNumber;
    private byte[] magicNumberBytes;
    /**
     * Block size
     */
    private int blockSize;
    private byte[] blockSizeBytes;
    /**
     * Block header
     */
    private BlockHeader blockHeader;
    /**
     * Transaction count
     */
    private int transactionCount;
    private byte[] transactionCountBytes;
    /**
     * Transactions
     */
    private List<Transaction> transactions;

    public Block(InputStream inputStream, int blockNumber, boolean debug) throws IOException {
        super(inputStream, debug);

        this.blockNumber = blockNumber;
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void build() throws IOException, NoSuchAlgorithmException {
        // Get the magic number and remove the bytes it occupied
        magicNumberBytes = pullBytes(magicNumberLengthInBytes, "block, magic number");
        magicNumber = EndiannessHelper.BytesToInt(magicNumberBytes);

        // Validate that the magic number matches the spec
        if (magicNumber != requiredMagicNumber) {
            throw new UnsupportedOperationException("Expected " + requiredMagicNumber + " for the magic number, saw " + magicNumber);
        }

        // Get the block size and remove the bytes it occupied
        blockSizeBytes = pullBytes(blockSizeLengthInBytes, "block, block size");
        blockSize = EndiannessHelper.BytesToInt(blockSizeBytes);

        // Sanity check the block size
        if (blockSize <= 0) {
            // This should never happen unless blocks get larger than 2 GB and we're wrapping around
            throw new UnsupportedOperationException("The block size is less than or equal to 0, saw " + blockSize);
        }

        // Get the block header and remove the bytes it occupied
        blockHeader = new BlockHeader(inputStream, isDebug());
        blockHeader.build();

        // Get the transaction count and return the remaining bytes back into the block header byte list
        VariableLengthInteger temp = new VariableLengthInteger(inputStream, isDebug());
        transactionCountBytes = temp.getValueBytes();
        transactionCount = (int) temp.getValue();

        // Sanity check transaction count
        if (transactionCount <= 0) {
            throw new UnsupportedOperationException("Transaction count cannot be less than or equal to 0, saw " + transactionCount);
        }

        // Initialize the transaction list
        transactions = new ArrayList<Transaction>();

        for (int transactionCounter = 0; transactionCounter < transactionCount; transactionCounter++) {
            Transaction transaction = new Transaction(inputStream, transactionCounter, isDebug());
            transaction.build();

            transactions.add(transaction);
        }

        // Calculate the Merkle root
        byte[] calculatedMerkleRoot = calculateMerkleRoot();

        // Validate that the Merkle root we calculated matches
        if (!Arrays.equals(calculatedMerkleRoot, blockHeader.getMerkleRoot())) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Merkle roots do not match\n\tCalculated: ");
            stringBuilder.append(ByteArrayHelper.formatArray(calculatedMerkleRoot));
            stringBuilder.append("\n\tFrom block chain: ");
            stringBuilder.append(ByteArrayHelper.formatArray(blockHeader.getMerkleRoot()));

            throw new UnsupportedOperationException(stringBuilder.toString());
        }
    }

    public byte[] calculateMerkleRoot() throws IOException, NoSuchAlgorithmException {
        if ((transactions == null) || (transactions.size() == 0)) {
            throw new UnsupportedOperationException("Cannot calculate the Merkle root with no transactions");
        }

        List<byte[]> transactionBytes = new ArrayList<byte[]>();

        // Build the base of the tree by dumping all of the hashes of the transaction bytes into an array
        for (Transaction transaction : transactions) {
            transactionBytes.add(HashHelper.doubleSha256Hash(transaction.dumpBytes()));
        }

        // Sort the hashes
        getLogger().info("Block number: " + blockNumber + " " + transactionBytes.size() + " transaction(s)");
        getLogger().info("Transactions: " + transactionCount);

        // Is there only one value?
        if (transactionBytes.size() == 1) {
            // Yes, there is only one value.  Just return it.
            return transactionBytes.get(0);
        }

        Collections.sort(transactionBytes, new HashComparator());

        getLogger().info("Merkle root calculation:");

       int level = 0;

        // Keep looping and hashing until there is only one value
        do {
            // Is the number of transactions odd?
            if ((transactionBytes.size() % 2) == 1) {
                // Yes, duplicate the last transaction hash
                transactionBytes.add(transactionBytes.get(transactionBytes.size() - 1));
            }

            logTree(level++, transactionBytes);

            // Copy the original data
            List<byte[]> tempTransactionBytes = new ArrayList<byte[]>(transactionBytes);

            // Clear out the original data
            transactionBytes = new ArrayList<byte[]>();

            // Combine the hashed values into the next level of the tree
            for (int loop = 0; loop < (tempTransactionBytes.size() / 2); loop++) {
                transactionBytes.add(HashHelper.doubleSha256Hash(ByteArrayHelper.concatenate(tempTransactionBytes.get(loop * 2), tempTransactionBytes.get((loop * 2) + 1))));
            }

            logTree(level++, transactionBytes);
        } while (transactionBytes.size() != 1);

        return transactionBytes.get(0);
    }

    private List<byte[]> fillInMerkleTreeRoot(List<byte[]> transactionBytes) {
        // Sanity check.  Make sure the data isn't NULL.
        if (transactionBytes == null) {
            throw new UnsupportedOperationException("Transaction bytes cannot be NULL in fillInMerkleTreeRoot");
        }

        // Sanity check.  Make sure that we don't already have the right number of values.
        if (numberOfBitsSet(transactionBytes.size()) == 1) {
            return transactionBytes;
        }

        // Now we know we need to fill in the tree.  Find the next power of two.
        int startingSize = transactionBytes.size();
        long nextPowerOfTwo = getNextPowerOfTwo(startingSize);
        int valuesToCopy = (int) (nextPowerOfTwo - startingSize);

        // Loop from the end of our list and fill in until we get to the next power of two
        for (int loop = 0; loop < valuesToCopy; loop++) {
            int indexToCopy = (int) ((startingSize - valuesToCopy) + loop);
            transactionBytes.add(transactionBytes.get(indexToCopy));
        }

        return transactionBytes;
    }

    private void logTree(int level, List<byte[]> transactionBytes) {
        getLogger().info("Level " + level + ":");

        int counter = 0;
        for (byte[] bytes : transactionBytes) {
            getLogger().info("Entry #" + counter + ": " + ByteArrayHelper.formatArray(bytes));
            counter++;
        }
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Block #");
            stringBuilder.append(blockNumber);
            stringBuilder.append(" data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tMagic number: ", "\n", magicNumberBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tBlock size: ", "\n", blockSizeBytes);
        stringBuilder.append(blockHeader.dump(pretty));
        DumpHelper.dump(stringBuilder, pretty, "\tTransaction count: ", "\n", transactionCountBytes);

        for (Transaction transaction : transactions) {
            stringBuilder.append(transaction.dump(pretty));
        }

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        bytes.write(magicNumberBytes);
        bytes.write(blockSizeBytes);
        bytes.write(blockHeader.dumpBytes());
        bytes.write(transactionCountBytes);

        for (Transaction transaction : transactions) {
            bytes.write(transaction.dumpBytes());
        }

        return bytes.toByteArray();
    }

    private int numberOfBitsSet(long value) {
        // "Variable precision SWAR algorithm" from Stack Overflow

        // Only allow 32-bit values
        long returnValue = value & 0xFFFFFFFFL;

        returnValue = returnValue - ((returnValue >> 1) & 0x55555555L);
        returnValue = (returnValue & 0x33333333L) + ((returnValue >> 2) & 0x33333333L);
        returnValue = (((returnValue + (returnValue >> 4)) & 0x0F0F0F0FL) * 0x01010101L) >> 24;

        return (int) returnValue;
    }

    private long getNextPowerOfTwo(long value) {
        // TODO - Need to do this without a loop

        // Only allow 32-bit values
        long maskedValue = value & 0xFFFFFFFFL;

        long currentBit = 0x00000001L;

        // Loop until we find that the current bit is larger than the masked value or the current bit is 0x80000000
        while (((currentBit <= maskedValue) && (currentBit != 0x80000000L))) {
            currentBit <<= 1;
        }

        // Is the current bit 0x80000000?
        if (currentBit == 0x80000000L) {
            throw new UnsupportedOperationException("Next power of two too large");
        } else {
            // No, return the value
            return currentBit;
        }
    }
}
