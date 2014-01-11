package com.timmattison.cryptocurrency.fakes;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.standard.StandardBlockChain;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class FakeBlockChain extends StandardBlockChain {
    @Inject
    public FakeBlockChain(BlockFactory blockFactory, BlockValidator blockValidator) throws IOException {
        super(blockFactory, blockValidator);
    }

    @Override
    public Block next() {
        try {
            // Are there bytes available?
            if (InputStreamHelper.getAvailableBytes(inputStream) > 0) {
                // Yes, create and parse the block
                Block block = blockFactory.createBlock(inputStream);

                // Return it right away
                return block;
            }
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

        return null;
    }
}
