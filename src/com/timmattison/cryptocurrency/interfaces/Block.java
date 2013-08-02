package com.timmattison.cryptocurrency.interfaces;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:31 AM
 * To change this template use File | Settings | File Templates.
 */
public interface Block extends Iterator<Transaction> {
    BlockHeader getBlockHeader();

    boolean isParentOf(Block block);

    void build();
}
