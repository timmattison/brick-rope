package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public class NopBlockStorage implements BlockStorage {
    @Override
    public Block getBlock(int blockNumber) {
        return null;
    }

    @Override
    public void storeBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException {
    }

    @Override
    public Transaction getTransaction(byte[] transactionHash) {
        return null;
    }
}
