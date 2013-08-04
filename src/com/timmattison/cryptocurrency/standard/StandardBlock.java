package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/1/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StandardBlock implements Block {
    private final byte[] data;
    private byte[] dataAfterBlockHeader;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private List<Transaction> transactions;
    private BlockHeader blockHeader = null;

    public StandardBlock(byte[] data, BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        this.data = data;
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
    }

    @Override
    public BlockHeader getBlockHeader() {
        if (blockHeader == null) {
            // Are there bytes available?
            if (data.length > 0) {
                // Yes, create and parse the block
                blockHeader = blockHeaderFactory.createBlockHeader(data);
                dataAfterBlockHeader = blockHeader.build();
            } else {
                throw new IllegalStateException("No bytes available when trying to build the block header");
            }
        }

        return blockHeader;
    }

    @Override
    public void build() {
        // Make sure the block header has been read already
        getBlockHeader();

        byte[] tempData = Arrays.copyOf(dataAfterBlockHeader, dataAfterBlockHeader.length);

        int position = 0;
        transactions = new ArrayList<Transaction>();

        // Are there bytes available?
        while ((tempData != null) && (tempData.length > 0)) {
            // Yes, create and parse the block
            Transaction transaction = transactionFactory.createTransaction(Arrays.copyOf(tempData, tempData.length));
            tempData = transaction.build();

            transactions.add(transaction);
        }
    }

    @Override
    public List<Transaction> getTransactions() {
        if (transactions == null) {
            build();
        }

        return transactions;
    }
}
