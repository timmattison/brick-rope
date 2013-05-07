package com.timmattison.bitcoin.test;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlockChain extends ByteConsumer {
    private static final String name = "BLOCK CHAIN";
    public static int blockNumber = 0;
    private Map<String, Transaction> transactionMap = new HashMap<String, Transaction>();
    private List<Block> blocks = new ArrayList<Block>();
    private Block previousBlock;

    public BlockChain(InputStream inputStream, boolean debug) throws IOException {
        super(inputStream, debug);
    }

    @Override
    protected void build() throws IOException, NoSuchAlgorithmException {
        long availableBytes = inputStream.available() & 0xFFFFFFFFL;

        // Loop until there is no more input stream data available
        while (availableBytes > 0) {
            // Display the block number
            if ((blockNumber % 10000) == 0) {
                getLogger().info("Block #" + blockNumber);
            }

            // Increment the counter
            blockNumber++;

            byte[] previousBlockHash = null;

            // Is there a previous block?
            if(previousBlock != null) {
                // Yes, get its hash
                previousBlockHash = previousBlock.getHeaderHash();
            }

            // Create and parse the block
            Block block = new Block(inputStream, this, blockNumber, previousBlockHash, isDebug());
            block.build();

            // Update the previous block
            previousBlock = block;

            // TODO - Store the transactions by their hash
            block.storeTransactions(transactionMap);

            availableBytes = inputStream.available() & 0xFFFFFFFFL;
        }
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Block chain:\n");
        }

        for (Block block : blocks) {
            stringBuilder.append(block.dump(pretty));
        }

        return stringBuilder.toString();
    }

    @Override
    protected byte[] dumpBytes() throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        // Loop through all of the blocks
        for (Block block : blocks) {
            // Dump the bytes for each block
            bytes.write(block.dumpBytes());
        }

        // Return a byte array
        return bytes.toByteArray();
    }

    public Transaction getTransaction(byte[] transactionHash) {
        return transactionMap.get(ByteArrayHelper.formatArray(transactionHash));
    }
}
