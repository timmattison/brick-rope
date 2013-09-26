package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.standard.StandardBlockChain;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/10/13
 * Time: 7:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockChainFactory implements BlockChainFactory {
    private BlockFactory blockFactory;
    private BlockValidator blockValidator;

    @Inject
    public BitcoinBlockChainFactory(BlockFactory blockFactory, BlockValidator blockValidator) {
        this.blockFactory = blockFactory;
        this.blockValidator = blockValidator;
    }

    @Override
    public BlockChain getBlockChain(String filename) {
        try {
            File inputFile = new File(filename);
            FileInputStream inputStream = new FileInputStream(inputFile);

            BlockChain blockChain = new StandardBlockChain(blockFactory, blockValidator);
            blockChain.setInputStream(inputStream);

            return blockChain;
        } catch (Exception ex) {
            throw new UnsupportedOperationException(ex);
        }
    }
}
