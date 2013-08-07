package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.timmattison.cryptocurrency.bitcoin.factories.*;
import com.timmattison.cryptocurrency.factories.*;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.StandardBlockChain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

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
        bind(Block.class).to(BitcoinBlock.class);

        bind(TransactionFactory.class).to(BitcoinTransactionFactory.class);
        bind(BlockFactory.class).to(BitcoinBlockFactory.class);
        bind(BlockHeaderFactory.class).to(BitcoinBlockHeaderFactory.class);
        bind(InputFactory.class).to(BitcoinInputFactory.class);
        bind(OutputFactory.class).to(BitcoinOutputFactory.class);
        bind(ScriptFactory.class).to(BitcoinScriptFactory.class);
    }

    @Provides
    MessageDigest getMessageDigest() {
        try {
            return MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
