package com.timmattison.cryptocurrency.factories;

import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 6:53 PM
 * To change this template use File | Settings | File Templates.
 */
public interface TransactionFactory {
    Transaction createTransaction(InputStream inputStream);
}
