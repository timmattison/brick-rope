package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
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
    private byte[] dataAfterBlockHeader;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private List<Transaction> transactions;
    private BlockHeader blockHeader = null;

    @Inject
    public StandardBlock(BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
    }

    @Override
    public BlockHeader getBlockHeader() {
        if (blockHeader == null) {
            throw new IllegalStateException("Block not built yet");
        }

        return blockHeader;
    }

    private void buildBlockHeader(byte[] data) {
        if(blockHeader != null) {
            // Are there bytes available?
            if (data.length > 0) {
                // Yes, create and parse the block
                blockHeader = blockHeaderFactory.createBlockHeader();
                dataAfterBlockHeader = blockHeader.build(data);
            } else {
                throw new IllegalStateException("No bytes available when trying to build the block header");
            }
        }
    }

    @Override
    public byte[] build(byte[] data) {
        // Make sure the block header has been read already
        buildBlockHeader(data);

        byte[] tempData = Arrays.copyOf(dataAfterBlockHeader, dataAfterBlockHeader.length);

        int position = 0;
        transactions = new ArrayList<Transaction>();

        // Are there bytes available?
        while ((tempData != null) && (tempData.length > 0)) {
            // Yes, create and parse the block
            Transaction transaction = transactionFactory.createTransaction();
            tempData = transaction.build(Arrays.copyOf(tempData, tempData.length));

            transactions.add(transaction);
        }

        return tempData;
    }

    @Override
    public List<Transaction> getTransactions() {
        if (transactions == null) {
            throw new IllegalStateException("Block has not been built yet");
        }

        return transactions;
    }
}
