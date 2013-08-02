package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/1/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StandardBlock implements Block {
    private final InputStream inputStream;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    BlockHeader blockHeader = null;

    public StandardBlock(InputStream inputStream, BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        this.inputStream = inputStream;
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
    }

    @Override
    public BlockHeader getBlockHeader() {
        if(blockHeader == null) {
            // Are there bytes available?
            if (InputStreamHelper.getAvailableBytes(inputStream) > 0) {
                // Yes, create and parse the block
                blockHeader = blockHeaderFactory.createBlockHeader(inputStream);
                blockHeader.build();
            }
            else {
                throw new IllegalStateException("No bytes available when trying to build the block header");
            }
        }

        return blockHeader;
    }

    @Override
    public boolean hasNext() {
        // Do we have any bytes left?
        if(InputStreamHelper.getAvailableBytes(inputStream) > 0) {
            // Yes, assume there is another transaction.  This may not be true if the bitstream is incomplete.
            return true;
        }
        else {
            // No, we must be done
            return false;
        }
    }

    @Override
    public Transaction next() {
        // Make sure the block header has been read already
        getBlockHeader();

        // Are there bytes available?
        if (InputStreamHelper.getAvailableBytes(inputStream) > 0) {
            // Yes, create and parse the block
            Transaction transaction = transactionFactory.createTransaction(inputStream);
            transaction.build();

            return transaction;
        }
        else {
            return null;
        }
    }

    @Override
    public void remove() {
        //To change body of implemented methods use File | Settings | File Templates.
        throw new NotImplementedException();
    }
}
