package com.timmattison.cryptocurrency.interfaces;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionListValidator {
    boolean isValid(long blockNumber, List<Transaction> transactionList);
}
