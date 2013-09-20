package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFastTransactionLocator implements TransactionLocator {
    @Inject
    public BitcoinFastTransactionLocator() {
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
