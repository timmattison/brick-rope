package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlock;import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockReader;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.standard.StandardBlock;

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
    private final InputStream inputStream;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private BitcoinBlockReader bitcoinBlockReader;

    public BitcoinBlockFactory(InputStream inputStream, BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory) {
        this.inputStream = inputStream;
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
    }
    @Override
    public Block createBlock() throws IOException {
        if(bitcoinBlockReader == null) {
            bitcoinBlockReader = new BitcoinBlockReader(inputStream);
        }

        return new BitcoinBlock(bitcoinBlockReader.getNextBlock(), blockHeaderFactory, transactionFactory);
    }
}
