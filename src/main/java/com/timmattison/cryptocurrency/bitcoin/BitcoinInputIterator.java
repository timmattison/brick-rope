package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Transaction;

/**
 * Created by timmattison on 9/29/14.
 */
public interface BitcoinInputIterator {
    public void iterateOverInputs(Transaction transaction, BitcoinInputProcessor bitcoinInputProcessor);
}
