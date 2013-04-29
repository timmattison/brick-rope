package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;

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

    public BlockChain(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
    }

    @Override
    protected void build() throws IOException {
        // Loop until there is no more input stream data available
        while (inputStream.available() > 0) {
            // Display the block number
            //if ((blockNumber % 10000) == 0) {
                getLogger().info("Block #" + blockNumber);
            //}

            // Increment the counter
            blockNumber++;

            // Create and parse the block
            Block block = new Block(inputStream, isDebug(), isInnerDebug());

            // Show the block's debug info
            block.showDebugInfo();
        }
    }

    @Override
    protected void initialize(Object[] objects) {
        throw new UnsupportedOperationException("Additional initialization not necessary");
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
