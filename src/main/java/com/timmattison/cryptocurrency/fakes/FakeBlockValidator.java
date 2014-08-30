package com.timmattison.cryptocurrency.fakes;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;

/**
 * Created by timmattison on 1/10/14.
 */
public class FakeBlockValidator implements BlockValidator {
    @Override
    public boolean hasValidHeaders(Block block) {
        return false;
    }

    @Override
    public boolean isValid(Block block, long blockNumber) {
        return true;
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        return true;
    }
}
