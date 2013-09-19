package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTransactionLocator implements TransactionLocator {
    private final BlockChainFactory blockChainFactory;

    @Inject
    public BitcoinTransactionLocator(BlockChainFactory blockChainFactory) {
        this.blockChainFactory = blockChainFactory;
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        BlockChain blockChain = blockChainFactory.getBlockChain();

        // Loop until we find something or until we run out of blocks
        while (blockChain.hasNext()) {
            Block block = blockChain.next();

            // Loop through all of this block's transactions
            for (Transaction transaction : block.getTransactions()) {
                // Does this transaction's hash match the transaction we're looking for?
                if (Arrays.equals(transactionHash, transaction.getHash())) {
                    // Yes, return this transaction
                    return transaction;
                }
            }
        }

        // Didn't find anything
        return null;
    }
}
