package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFastTransactionLocator implements TransactionLocator {
    private final BlockChainFactory blockChainFactory;
    private BlockChainIndex blockChainIndex;

    @Inject
    public BitcoinFastTransactionLocator(BlockChainFactory blockChainFactory) {
        this.blockChainFactory = blockChainFactory;
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        blockChainIndex = getBlockChainIndex();

        if (bloc)
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

    public BlockChainIndex getBlockChainIndex() {
        return blockChainIndex;
    }

    private class BlockChainIndex {
        /**
         * The last byte position that data was read and processed from
         */
        public long lastPositionRead;

        /**
         * The offset of a particular block in the block chain
         */
        public Map<Integer, Long> blockOffsets;

        /**
         * Which block contains a particular transaction
         */
        public Map<byte[], Integer> transactionBlockLocation;
    }
}
