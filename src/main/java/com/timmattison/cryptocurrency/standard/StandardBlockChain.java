package com.timmattison.cryptocurrency.standard;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class StandardBlockChain implements BlockChain, Iterator<Block> {
    protected final BlockFactory blockFactory;
    private final BlockValidator blockValidator;
    private int blockNumber;
    protected InputStream inputStream;
    private Block previousBlock = null;
    private long position;

    @Inject
    public StandardBlockChain(BlockFactory blockFactory, BlockValidator blockValidator) throws IOException {
        this.blockFactory = blockFactory;
        this.blockValidator = blockValidator;
        blockNumber = 0;
        position = 0;
    }

    @Override
    public boolean hasNext() {
        // Do we have any bytes left?
        if (InputStreamHelper.getAvailableBytes(inputStream) > 0) {
            // Yes, assume there is another block.  This may not be true if the bitstream is incomplete.
            return true;
        } else {
            // No, we must be done
            return false;
        }
    }

    @Override
    public Block next() {
        try {
            // Are there bytes available?
            if (InputStreamHelper.getAvailableBytes(inputStream) > 0) {
                // Yes, create and parse the block
                Block block = blockFactory.createBlock(inputStream);

                // Keep track of how many bytes we've read
                position += block.dump().length;

                // Validate the block
                if (!blockValidator.isValid(block)) {
                    // No, current block is not valid
                    throw new IllegalStateException("Block [" + blockNumber + "] is not valid");
                }

                // Is the previous block a valid parent of this block?
                if ((previousBlock != null) && (!blockValidator.isParentOf(previousBlock, block))) {
                    // No, this is an issue
                    throw new IllegalStateException("Previous block is not the parent of the current block [" + blockNumber + "]");
                }

                // Update the previous block
                previousBlock = block;

                blockNumber++;
                return block;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void setInputStream(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public long getPosition() {
        return position;
    }

    @Override
    public void skip(long position) {
        try {
            inputStream.skip(position);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }
}
