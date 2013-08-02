package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.StandardBlock;
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
        bind(BlockChain.class).to(StandardBlockChain.class);
        bind(Block.class).to(StandardBlock.class);
        bind(BlockHeader.class).to(BitcoinBlockHeader.class);
        bind(Transaction.class).to(BitcoinTransaction.class);
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
