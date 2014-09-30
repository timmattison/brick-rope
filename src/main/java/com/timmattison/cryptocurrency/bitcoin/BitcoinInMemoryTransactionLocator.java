package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Transaction;

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
public class BitcoinInMemoryTransactionLocator extends AbstractTransactionLocator {
    private final BlockChainFactory blockChainFactory;
    private BlockChain blockChain;
    private Map<String, Transaction> transactionMap = new HashMap<String, Transaction>();

    @Inject
    public BitcoinInMemoryTransactionLocator(TransactionFactory transactionFactory, BlockChainFactory blockChainFactory) {
        super(transactionFactory);
        this.blockChainFactory = blockChainFactory;
    }

    @Override
    public Transaction innerFindTransaction(byte[] transactionHash) {
        if (true) {
            throw new UnsupportedOperationException("This is not supported anymore!");
        }

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
