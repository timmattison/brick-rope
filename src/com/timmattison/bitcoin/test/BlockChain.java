package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
    private List<Block> blocks = new ArrayList<Block>();

    public BlockChain(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
    }

    @Override
    protected void build() throws IOException {
        long availableBytes = inputStream.available() & 0xFFFFFFFFL;

        // Loop until there is no more input stream data available
        while (availableBytes > 0) {
            // Display the block number
            if ((blockNumber % 10000) == 0) {
                getLogger().info("Block #" + blockNumber);
            }

            // Increment the counter
            blockNumber++;

            // Create and parse the block
            Block block = new Block(inputStream, isDebug(), isInnerDebug());
            block.build();

            // Show the block's debug info
            block.showDebugInfo();

            availableBytes = inputStream.available() & 0xFFFFFFFFL;
        }
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Block chain:\n");
        }

        for(Block block : blocks) {
            stringBuilder.append(block.dump(pretty));
        }

        return stringBuilder.toString();
    }
}
