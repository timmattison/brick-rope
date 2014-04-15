package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.interfaces.Block;

import java.sql.SQLException;

/**
 * Created by timmattison on 4/15/14.
 */
public interface BlockStorage {
    public Block getBlock(int blockNumber);

    public void storeBlock(int blockNumber, Block block) throws SQLException, ClassNotFoundException;
}
