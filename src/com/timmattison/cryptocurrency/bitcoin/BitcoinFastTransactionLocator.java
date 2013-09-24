package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.HashMap;
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
    private BlockChain blockChain;
    private Map<String, Transaction> transactionMap = new HashMap<String, Transaction>();
    private int blockNumber = 0;

    @Inject
    public BitcoinFastTransactionLocator(BlockChainFactory blockChainFactory) {
        this.blockChainFactory = blockChainFactory;
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        String transactionHashString = ByteArrayHelper.toHex(transactionHash);

        Transaction result = innerGetTransaction(transactionHashString);

        // Do we have it already?
        if (result != null) {
            // Yes, just return it
            return result;
        }

        // Loop until we find something or until we run out of blocks
        while (getBlockChain().hasNext()) {
            Block block = getBlockChain().next();
            blockNumber++;

            // Loop through all of this block's transactions and add them to the map
            for (Transaction transaction : block.getTransactions()) {
                String newTransactionHashString = ByteArrayHelper.toHex(transaction.getHash());
                transactionMap.put(newTransactionHashString, transaction);
            }

            // Does this transaction's hash match the transaction we're looking for?
            if ((result = innerGetTransaction(transactionHashString)) != null) {
                // Yes, return this transaction
                return result;
            }
        }

        // Didn't find anything
        return null;
    }

    private Transaction innerGetTransaction(String transactionHashString) {
        // Do we have it already?
        if (transactionMap.containsKey(transactionHashString)) {
            // Yes, just return it
            return transactionMap.get(transactionHashString);
        }

        // Didn't find it yet
        return null;
    }

    private BlockChain getBlockChain() {
        if (blockChain == null) {
            blockChain = blockChainFactory.getBlockChain();
        }

        return blockChain;
    }
}
