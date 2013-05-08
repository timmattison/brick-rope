package com.timmattison.bitcoin.test;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
    private BlockChain blockChain;
    private int blockNumber;
    private byte[] previousBlockHash;
    private byte[] headerHash;
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

    public Block(InputStream inputStream, BlockChain blockChain, int blockNumber, byte[] previousBlockHash, boolean debug) throws IOException {
        super(inputStream, debug);

        this.blockChain = blockChain;
        this.blockNumber = blockNumber;
        this.previousBlockHash = previousBlockHash;
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

        // Validate the block
        validateBlock();

        // Check output types
        for (Transaction transaction : transactions) {
            for (Output output : transaction.getOutputs()) {
                OutputType outputType = OutputClassifier.getOutputType(output);

                if (!outputType.equals(OutputType.SingleSigned) && !outputType.equals(OutputType.Unknown)) {
                    getLogger().info("Block #" + blockNumber + ": " + String.valueOf(OutputClassifier.getOutputType(output)));
                }
            }
        }

        if(blockNumber == 9) {
            getLogger().info(transactions.get(0).getOutput(0).dump(true));
        }

        // Is there a second transaction?
        if (transactions.size() > 1) {
            // Yes, get the second input and output
            Input input = transactions.get(1).getInput(0);

            byte[] previousTransactionHash = input.getPreviousTransactionHash();
            long previousOutputIndex = input.getPreviousOutputIndex();

            // Get the referenced transaction
            Transaction referencedTransaction = blockChain.getTransaction(previousTransactionHash);

            // Did we find it?
            if (referencedTransaction == null) {
                // No, throw an exception
                throw new UnsupportedOperationException("Couldn't find the block reference by this transaction");
            }

            Output output = referencedTransaction.getOutput((int) previousOutputIndex);
            getLogger().info(output.dump(true));
            getLogger().info(ByteArrayHelper.formatArray(output.getScript().dumpBytes()));

            // Combine the input and the output
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(ByteArrayHelper.concatenate(input.getScript().dumpBytes(), output.getScript().dumpBytes()));
            Script fullScript = new Script(byteArrayInputStream, output.getScript().getVersionNumber(), isDebug());

            try {
                // Build and execute the full script and see if it throws an exception
                fullScript.build();
                fullScript.execute();
            }
            catch (ScriptExecutionException ex) {

            }
        }
    }

    private Output getCoinbaseOutput() {
        return transactions.get(0).getOutput(0);
    }

    public byte[] getHeaderHash() throws IOException, NoSuchAlgorithmException {
        if (headerHash == null) {
            headerHash = HashHelper.doubleSha256Hash(blockHeader.dumpBytes());
        }

        return headerHash;
    }

    private void validateBlock() throws IOException, NoSuchAlgorithmException {
        // Do we have the hash of the previous block?
        if (previousBlockHash != null) {
            // Yes, does it match what we
            if (!Arrays.equals(previousBlockHash, blockHeader.getPreviousBlockHash())) {
                // No, throw an exception
                throw new UnsupportedOperationException("Previous block hash doesn't match what this block has for its previous block hash");
            }
        }

        // Calculate the Merkle root
        byte[] calculatedMerkleRoot = calculateMerkleRoot();

        // Validate that the Merkle root we calculated matches
        validateMerkleRoot(calculatedMerkleRoot);

        // Validate the hash is lower than the target
        validateHashAgainstTarget();
    }

    private void validateHashAgainstTarget() throws NoSuchAlgorithmException {
        BigInteger currentHash = blockHeader.getHashBigInteger();
        BigInteger currentTarget = blockHeader.getTargetBigInteger();

        // Is the current hash less than the current target?
        if (currentHash.compareTo(currentTarget) >= 0) {
            // No, it is equal to or greater than the current target.  Throw an exception.
            throw new UnsupportedOperationException("Current hash is greater than the current target");
        }
    }

    private void validateMerkleRoot(byte[] calculatedMerkleRoot) {
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
            transactionBytes.add(transaction.getHash());
        }

        // Is there only one value?
        if (transactionBytes.size() == 1) {
            // Yes, there is only one value.  Just return it.
            return transactionBytes.get(0);
        }

        Collections.sort(transactionBytes, new HashComparator());

        int level = 0;

        // Keep looping and hashing until there is only one value
        do {
            // Is the number of transactions odd?
            if ((transactionBytes.size() % 2) == 1) {
                // Yes, duplicate the last transaction hash
                transactionBytes.add(transactionBytes.get(transactionBytes.size() - 1));
            }

            //logTree(level++, transactionBytes);

            // Copy the original data
            List<byte[]> tempTransactionBytes = new ArrayList<byte[]>(transactionBytes);

            // Clear out the original data
            transactionBytes = new ArrayList<byte[]>();

            // Combine the hashed values into the next level of the tree
            for (int loop = 0; loop < (tempTransactionBytes.size() / 2); loop++) {
                transactionBytes.add(HashHelper.doubleSha256Hash(ByteArrayHelper.concatenate(tempTransactionBytes.get(loop * 2), tempTransactionBytes.get((loop * 2) + 1))));
            }
        } while (transactionBytes.size() != 1);

        return transactionBytes.get(0);
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

    public void storeTransactions(Map<String, Transaction> transactionMap) throws IOException, NoSuchAlgorithmException {
        int counter = 0;

        for(Transaction transaction : transactions) {
            byte[] transactionHash = transaction.getHash();
            String hashString = ByteArrayHelper.formatArray(transactionHash);
            transactionMap.put(hashString, transaction);

            getLogger().info("Block #" + blockNumber + ", transaction #" + counter++ + ", " + hashString);
        }
    }
}
