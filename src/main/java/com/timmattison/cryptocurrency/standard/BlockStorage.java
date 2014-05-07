package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public interface BlockStorage {
    public Block getBlock(int blockNumber) throws SQLException, ClassNotFoundException, IOException;

    public void storeBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException;

    public Transaction getTransaction(byte[] transactionHash) throws SQLException, ClassNotFoundException, IOException;
}
