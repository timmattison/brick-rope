package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Transaction;

/**
 * Created by timmattison on 4/15/14.
 */
public interface TransactionStorage {
    public Transaction getTransaction(byte[] hash);

    public void storeTransaction(byte[] hash, Transaction transaction);
}
