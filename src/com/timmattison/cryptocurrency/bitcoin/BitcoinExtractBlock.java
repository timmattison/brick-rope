package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Guice;
import com.google.inject.Injector;
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

        BlockChain blockChain = injector.getInstance(BlockChain.class);

        File inputFile = new File("bitcoin-blockchain.dat");
        FileInputStream inputStream = new FileInputStream(inputFile);

        blockChain.setInputStream(inputStream);

        for (int loop = 0; loop < 172; loop++) {
            Block block = blockChain.next();
            File outputFile = new File("bitcoin-block-" + String.format("%06d", loop) + ".dat");

            OutputStream outputStream = new FileOutputStream(outputFile);
            outputStream.write(block.dump());
        }
    }
}
