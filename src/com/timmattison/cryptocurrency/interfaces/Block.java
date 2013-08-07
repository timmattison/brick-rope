package com.timmattison.cryptocurrency.interfaces;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Block extends Buildable {
    BlockHeader getBlockHeader();

    List<Transaction> getTransactions();
}
