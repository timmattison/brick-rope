package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockValidator implements BlockValidator {
    @Override
    public boolean isValid(Block block) {
        byte[] asdf = block.getBlockHeader().hash();
        asdf = null;
        return true;
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        //To change body of implemented methods use File | Settings | File Templates.
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
