package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.AbstractModule;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinBlockFactory;
import com.timmattison.cryptocurrency.factories.BlockFactory;
import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.StandardBlock;
import com.timmattison.cryptocurrency.standard.StandardBlockChain;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(BlockReader.class).to(BitcoinBlockReader.class);

        bind(BlockChain.class).to(StandardBlockChain.class);
        bind(Block.class).to(BitcoinBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Transaction.class).to(BitcoinTransaction.class);
        bind(Block.class).to(BitcoinBlock.class);

        bind(BlockFactory.class).to(BitcoinBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
    }

    //@Provides
    //MessageDigest getMessageDigest() {
    //    try {
    //        return MessageDigest.getInstance("SHA-256");
    //    } catch (NoSuchAlgorithmException e) {
    //        throw new IllegalStateException(e);
    //    }
    //}
}
