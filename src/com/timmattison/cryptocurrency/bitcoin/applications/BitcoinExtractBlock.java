package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinExtractBlock {
    public static void main(String[] args) throws IOException {
        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();

        int blockNumber = 0;
        Block block = blockChain.next();

        while(block != null) {
            System.out.println("Block #" + blockNumber);
            File outputFile = new File("blocks/bitcoin-block-" + String.format("%06d", blockNumber) + ".dat");

            OutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(block.dump());

            block = blockChain.next();
        }
    }
}
