package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinTransaction;
import com.timmattison.cryptocurrency.factories.InputFactory;
import com.timmattison.cryptocurrency.factories.OutputFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
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
    private final InputFactory inputFactory;
    private final OutputFactory outputFactory;
    private final HasherFactory hasherFactory;
    private final VariableLengthIntegerFactory variableLengthIntegerFactory;

    @Inject
    public BitcoinTransactionFactory(InputFactory inputFactory, OutputFactory outputFactory, HasherFactory hasherFactory, VariableLengthIntegerFactory variableLengthIntegerFactory) {
        this.inputFactory = inputFactory;
        this.outputFactory = outputFactory;
        this.hasherFactory = hasherFactory;
        this.variableLengthIntegerFactory = variableLengthIntegerFactory;
    }

    @Override
    public Transaction createTransaction(int transactionNumber) {
        return new BitcoinTransaction(inputFactory, outputFactory, hasherFactory, transactionNumber, variableLengthIntegerFactory);
    }
}
