package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/1/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class StandardBlock implements Block {
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private byte[] transactionCountBytes;
    private long transactionCount;
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

    @Override
    public byte[] build(byte[] data) {
        // Make sure the block header has been read already
        blockHeader = blockHeaderFactory.createBlockHeader();
        data = blockHeader.build(data);

        // Get the transaction count and return the remaining bytes back
        VariableLengthInteger temp = new VariableLengthInteger();
        data = temp.build(data);
        transactionCountBytes = temp.getValueBytes();
        transactionCount = (int) temp.getValue();

        int transactionNumber = 0;
        transactions = new ArrayList<Transaction>();

        // Are there bytes available?
        while (transactionNumber < transactionCount) {
            // Yes, create and parse the block
            Transaction transaction = transactionFactory.createTransaction(transactionNumber);
            data = transaction.build(data);

            transactions.add(transaction);
            transactionNumber++;
        }

        return data;
    }

    @Override
    public List<Transaction> getTransactions() {
        if (transactions == null) {
            throw new IllegalStateException("Block has not been built yet");
        }

        return transactions;
    }

    @Override
    public byte[] dump() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            baos.write(blockHeader.dump());
            baos.write(transactionCountBytes);

            for (Transaction transaction : transactions) {
                baos.write(transaction.dump());
            }

            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public String prettyDump(int indentationLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        StringBuilder indentation = new StringBuilder();

        for (int loop = 0; loop < indentationLevel; loop++) {
            indentation.append("\t");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Block header: ");
        stringBuilder.append(blockHeader.prettyDump(indentationLevel + 1));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Transaction count: ");
        stringBuilder.append(ByteArrayHelper.toHex(transactionCountBytes));
        stringBuilder.append("\n");

        for (int loop = 0; loop < transactions.size(); loop++) {
            stringBuilder.append(indentation);
            stringBuilder.append("Transaction #");
            stringBuilder.append(loop);
            stringBuilder.append(transactions.get(loop).prettyDump(indentationLevel + 1));
        }

        return stringBuilder.toString();
    }
}
