package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlock;import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockReader;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockReader;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 10:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockFactory implements BlockFactory {
    private final BlockReader blockReader;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private BitcoinBlockReader bitcoinBlockReader;

    @Inject
    public BitcoinBlockFactory(BlockReader blockReader, BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        this.blockReader = blockReader;
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
    }

    @Override
    public Block createBlock(InputStream inputStream) throws IOException {
        if(bitcoinBlockReader == null) {
            bitcoinBlockReader = new BitcoinBlockReader();
        }

        BitcoinBlock bitcoinBlock = new BitcoinBlock(blockHeaderFactory, transactionFactory);
        bitcoinBlock.build(bitcoinBlockReader.getNextBlock(inputStream));

        return bitcoinBlock;
    }
}
