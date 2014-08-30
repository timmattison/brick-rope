package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockValidator implements BlockValidator {
    private final MerkleRootCalculator merkleRootCalculator;

    @Inject
    public BitcoinBlockValidator(MerkleRootCalculator merkleRootCalculator) {
        this.merkleRootCalculator = merkleRootCalculator;
    }

    @Override
    public boolean isValid(Block block) {
        // Calculate the Merkle root of this block and make sure that it matches
        boolean merkleRootMatches = Arrays.equals(block.getBlockHeader().getMerkleRoot(), merkleRootCalculator.calculateMerkleRoot(block));

        if(!merkleRootMatches) {
            // Doesn't match, fail
            return false;
        }

        // Check to see if the hash is below the target
        boolean hashIsBelowTarget = block.getBlockHeader().getTarget().isBelowHash(block.getBlockHeader().getHashBigInteger());

        if(!hashIsBelowTarget) {
            // Isn't below target, fail
            return false;
        }

        // Looks good
        return true;
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        return Arrays.equals(childBlock.getBlockHeader().getPreviousBlockHash(), parentBlock.getBlockHeader().getHash().getOutput());
    }
}
