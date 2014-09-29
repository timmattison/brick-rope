package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;

/**
 * Created by timmattison on 9/29/14.
 */
public interface BitcoinInputProcessor {
    public void process(Transaction transaction, int inputNumber, Input input);
}
