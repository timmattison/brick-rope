package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public class NopBlockStorage implements BlockStorage {
    @Override
    public Block getBlock(long blockNumber) throws SQLException, ClassNotFoundException, IOException {
        return null;
    }

    @Override
    public void storeBlock(long blockNumber, Block block) throws SQLException, ClassNotFoundException, IOException {

    }

    @Override
    public Transaction getTransaction(byte[] transactionHash) {
        return null;
    }
}
