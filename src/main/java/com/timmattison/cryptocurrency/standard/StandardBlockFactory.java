package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlock;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
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
public class StandardBlockFactory implements BlockFactory {
    private final BlockReader blockReader;
    private final BlockHeaderFactory blockHeaderFactory;
    private final TransactionFactory transactionFactory;
    private final VariableLengthIntegerFactory variableLengthIntegerFactory;

    @Inject
    public StandardBlockFactory(BlockReader blockReader, BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory, VariableLengthIntegerFactory variableLengthIntegerFactory) {
        this.blockReader = blockReader;
        this.blockHeaderFactory = blockHeaderFactory;
        this.transactionFactory = transactionFactory;
        this.variableLengthIntegerFactory = variableLengthIntegerFactory;
    }

    @Override
    public Block createBlock(InputStream inputStream) throws IOException {
        BitcoinBlock bitcoinBlock = new BitcoinBlock(blockHeaderFactory, transactionFactory, variableLengthIntegerFactory);
        bitcoinBlock.build(blockReader.getNextBlock(inputStream));

        return bitcoinBlock;
    }
}
