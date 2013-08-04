package com.timmattison.cryptocurrency.standard;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;

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
    private final InputStream inputStream;
    private final BlockFactory blockFactory;
    private Block previousBlock = null;

    @Inject
    public StandardBlockChain(InputStream inputStream, BlockFactory blockFactory) throws IOException {
        this.inputStream = inputStream;
        this.blockFactory = blockFactory;
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
                Block block = blockFactory.createBlock();
                block.build();

                // Is the previous block a valid parent of this block?
                if ((previousBlock != null) && (!previousBlock.isParentOf(block))) {
                    // No, this is an issue
                    throw new IllegalStateException("Previous block is not the parent of the current block");
                }

                // Update the previous block
                previousBlock = block;

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
}
