package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractTransactionLocator implements TransactionLocator {
    private final TransactionFactory transactionFactory;
    private Map<String, Transaction> temporaryTransactions;

    @Inject
    public AbstractTransactionLocator(TransactionFactory transactionFactory) {
        this.transactionFactory = transactionFactory;
    }

    @Override
    public void addTemporaryBlock(Block block) {
        temporaryTransactions = new HashMap<String, Transaction>();

        for (Transaction transaction : block.getTransactions()) {
            temporaryTransactions.put(ByteArrayHelper.toHex(transaction.getHash()), transaction);
        }
    }

    private Transaction searchTemporaryTransactions(byte[] transactionHash) {
        if (temporaryTransactions == null) {
            return null;
        }

        Transaction transaction = temporaryTransactions.get(ByteArrayHelper.toHex(transactionHash));

        if (transaction == null) {
            return null;
        }

        Transaction copy = transactionFactory.createTransaction(transaction.getTransactionNumber());
        copy.build(transaction.dump());

        return copy;
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        Transaction transaction = searchTemporaryTransactions(transactionHash);

        if (transaction != null) {
            return transaction;
        }

        return innerFindTransaction(transactionHash);
    }

    protected abstract Transaction innerFindTransaction(byte[] transactionHash);
}
