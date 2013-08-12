package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.HashComparator;
import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockValidator implements BlockValidator {
    private final HasherFactory hasherFactory;
    private final MerkleRootCalculator merkleRootCalculator;

    @Inject
    public BitcoinBlockValidator(HasherFactory hasherFactory, MerkleRootCalculator merkleRootCalculator) {
        this.hasherFactory = hasherFactory;
        this.merkleRootCalculator = merkleRootCalculator;
    }

    @Override
    public boolean isValid(Block block) {
        boolean merkleRootMatches = Arrays.equals(block.getBlockHeader().getMerkleRoot(), merkleRootCalculator.calculateMerkleRoot(block));

        if(!merkleRootMatches) {
            return false;
        }

        boolean hashIsBelowTarget = block.getBlockHeader().getTarget().isBelowTarget(block.getBlockHeader().getHashBigInteger());

        if(!hashIsBelowTarget) {
            return false;
        }

        return true;
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        byte[] hash1 = childBlock.getBlockHeader().getPreviousBlockHash();
        byte[] hash2 = parentBlock.getBlockHeader().getHash();

        return Arrays.equals(childBlock.getBlockHeader().getPreviousBlockHash(), parentBlock.getBlockHeader().getHash());
    }
}
