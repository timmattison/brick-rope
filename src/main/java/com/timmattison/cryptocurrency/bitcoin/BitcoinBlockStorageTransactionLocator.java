package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

public class BitcoinBlockStorageTransactionLocator implements TransactionLocator {
    private final BlockStorage blockStorage;

    @Inject
    public BitcoinBlockStorageTransactionLocator(BlockStorage blockStorage) {
        this.blockStorage = blockStorage;
    }

    @Override
    public Transaction findTransaction(byte[] transactionHash) {
        try {
            return blockStorage.getTransaction(ByteArrayHelper.toHex(transactionHash));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new UnsupportedOperationException(e);
        }
    }
}
