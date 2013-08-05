package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinTransaction;
import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/5/13
 * Time: 10:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinTransactionFactory implements TransactionFactory {
    private InputFactory inputFactory;
    private OutputFactory outputFactory;

    @Inject
    public BitcoinTransactionFactory(InputFactory inputFactory, OutputFactory outputFactory) {
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
    }

    @Override
    public Transaction createTransaction(int transactionNumber) {
        return new BitcoinTransaction(inputFactory, outputFactory, transactionNumber);
    }
}
