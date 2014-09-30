package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;

import javax.inject.Inject;
import java.io.IOException;
import java.sql.SQLException;

public class BitcoinBlockStorageTransactionLocator extends AbstractTransactionLocator {
    private final BlockStorage blockStorage;

    @Inject
    public BitcoinBlockStorageTransactionLocator(TransactionFactory transactionFactory, BlockStorage blockStorage) {
        super(transactionFactory);
        this.blockStorage = blockStorage;
    }

    @Override
    public Transaction innerFindTransaction(byte[] transactionHash) {
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
