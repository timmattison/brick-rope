package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.interfaces.BlockFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.BlockHashCalculator;
import com.timmattison.cryptocurrency.interfaces.Hash;
import com.timmattison.cryptocurrency.interfaces.HashCalculator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockChain implements BlockChain, Iterator<Block> {
    private final InputStream inputStream;
    private final BlockFactory blockFactory;
    private Long blockCount;

    @Inject
    public BitcoinBlockChain(InputStream inputStream, BlockFactory blockFactory) throws IOException {
        this.inputStream = inputStream;
        this.blockFactory = blockFactory;
    }

    @Override
    protected void build() {
        long availableBytes = 0;
        try {
            availableBytes = inputStream.available() & 0xFFFFFFFFL;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        Block previousBlock = null;

        // Loop until there is no more input stream data available
        while (availableBytes > 0) {
            Hash previousBlockHash = null;

            // Create and parse the block
            Block block = blockFactory.createBlock();
            block.build(inputStream);

            // Is the previous block a valid parent of this block?
            if((previousBlock != null) && (!previousBlock.isParentOf(block))) {
                // No, this is an issue
                throw new IllegalStateException("Previous block is not the parent of the current block");
            }

            // Update the previous block
            previousBlock = block;

            try {
                availableBytes = inputStream.available() & 0xFFFFFFFFL;
            } catch (IOException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }
        }
    }

    @Override
    public long getBlockCount() {
        if(blockCount == null) {
            blockCount = new Long(0);

            Iterator<Block> blockIterator = getBlockIterator();

            while(blockIterator.hasNext()) {
                blockCount++;
                blockIterator.next();
            }
        }

        return blockCount;
    }

    @Override
    public Iterator<Block> getBlockIterator() {
        return this;
    }

    @Override
    public byte[] dump() {
        Iterator<Block> blockIterator = getBlockIterator();

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while(blockIterator.hasNext()) {
            try {
                byteArrayOutputStream.write(blockIterator.next().dump());
            } catch (IOException e) {
                throw new IllegalStateException(e);
            }
        }

        return byteArrayOutputStream.toByteArray();
    }

    @Override
    public String dumpPretty() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public boolean hasNext() {
        return false;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Block next() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void remove() {
        throw new IllegalStateException("Cannot remove blocks from a block chain");
    }
}
