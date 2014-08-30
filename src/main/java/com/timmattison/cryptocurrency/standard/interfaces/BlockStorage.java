package com.timmattison.cryptocurrency.standard.interfaces;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public interface BlockStorage {
    public int getBlockCount() throws SQLException, ClassNotFoundException;

    public int getLastBlockNumber() throws SQLException, ClassNotFoundException;

    public Long getBlockOffset(long blockNumber) throws SQLException, ClassNotFoundException, IOException;

    public Block getBlock(long blockNumber) throws SQLException, ClassNotFoundException, IOException;

    public void storeBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException;

    public Transaction getTransaction(String transactionHash) throws SQLException, ClassNotFoundException, IOException;
}
