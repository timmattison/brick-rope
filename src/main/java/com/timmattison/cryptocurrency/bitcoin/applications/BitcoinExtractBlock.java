package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.modules.BitcoinFastExtractorModule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinExtractBlock {
    private static final String outputDirectoryName = "blocks";
    private static final String outputFileHeader = "bitcoin-block-";
    private static final String outputFileSuffix = ".dat";

    public static void main(String[] args) throws IOException {
        int blockToExtract = 211914;

        Injector injector = Guice.createInjector(new BitcoinFastExtractorModule(blockToExtract));

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();

        Block block = blockChain.next();

        File outputDirectory = new File(outputDirectoryName);
        outputDirectory.mkdirs();

        File outputFile = new File(outputDirectory + "/" + outputFileHeader + String.format("%06d", blockToExtract) + outputFileSuffix);

        OutputStream outputStream = new FileOutputStream(outputFile);
        outputStream.write(block.dump());
    }
}
