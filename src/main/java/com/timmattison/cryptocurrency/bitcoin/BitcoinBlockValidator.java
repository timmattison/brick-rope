package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;
import com.timmattison.cryptocurrency.interfaces.TransactionListValidator;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockValidator implements BlockValidator {
    private final Logger logger;
    private final MerkleRootCalculator merkleRootCalculator;
    private final TransactionListValidator transactionListValidator;

    @Inject
    public BitcoinBlockValidator(Logger logger, MerkleRootCalculator merkleRootCalculator, TransactionListValidator transactionListValidator) {
        this.logger = logger;
        this.merkleRootCalculator = merkleRootCalculator;
        this.transactionListValidator = transactionListValidator;
    }

    @Override
    public boolean hasValidHeaders(Block block) {
        // Calculate the Merkle root of this block and make sure that it matches
        boolean merkleRootMatches = Arrays.equals(block.getBlockHeader().getMerkleRoot(), merkleRootCalculator.calculateMerkleRoot(block));

        if (!merkleRootMatches) {
            // Doesn't match, fail
            return false;
        }

        // Check to see if the hash is below the target
        boolean hashIsBelowTarget = block.getBlockHeader().getTarget().isBelowHash(block.getBlockHeader().getHashBigInteger());

        if (!hashIsBelowTarget) {
            // Isn't below target, fail
            return false;
        }

        // Looks good
        return true;
    }

    @Override
    public boolean isValid(Block block, long blockNumber) {
        // Check the headers first
        boolean headersAreValid = hasValidHeaders(block);

        if (!headersAreValid) {
            // Headers aren't valid, fail
            return false;
        }

        // Check to see if the transactions are valid
        boolean transactionsAreValid = transactionListValidator.isValid(blockNumber, block.getTransactions());

        if (!transactionsAreValid) {
            // Transactions aren't valid, fail
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
