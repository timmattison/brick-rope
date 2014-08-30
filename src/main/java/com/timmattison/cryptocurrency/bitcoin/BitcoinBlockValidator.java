package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.*;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;
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
    private final TransactionValidator transactionValidator;

    @Inject
    public BitcoinBlockValidator(Logger logger, MerkleRootCalculator merkleRootCalculator, TransactionValidator transactionValidator) {
        this.logger = logger;
        this.merkleRootCalculator = merkleRootCalculator;
        this.transactionValidator = transactionValidator;
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
        boolean transactionsAreValid = transactionsAreValid(block, blockNumber);

        if (!transactionsAreValid) {
            // Transactions aren't valid, fail
            return false;
        }

        // Looks good
        return true;
    }

    private boolean transactionsAreValid(Block block, long blockNumber) {
        // Get the transaction list
        List<Transaction> transactionList = block.getTransactions();

        // Does the block have any transactions other than the coinbase?
        if (transactionList.size() > 1) {
            // Yes, check them out
            logger.info((transactionList.size() - 1) + " transaction(s) other than the coinbase in block number " + blockNumber);
        } else {
            // No, do nothing
            //logger.info("Only a coinbase in block number " + blockNumber);
        }

        for (Transaction currentTransaction : transactionList) {
            if (currentTransaction.getTransactionNumber() == 0) {
                continue;
            }

            // Validate the transaction
            if (!transactionValidator.isValid(currentTransaction)) {
                logger.info("Transactions in block number " + blockNumber + " are not valid");
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        return Arrays.equals(childBlock.getBlockHeader().getPreviousBlockHash(), parentBlock.getBlockHeader().getHash().getOutput());
    }
}
