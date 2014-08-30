package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.modules.BitcoinNoLoopTestModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import org.junit.Assert;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinDumpBlockChainToH2 {
    private static boolean debug = false;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinNoLoopTestModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        BlockStorage blockStorage = injector.getInstance(BlockStorage.class);

        Block block = blockChain.next();
        int blockNumber = 0;

        long start = System.currentTimeMillis();

        try {
            while (block != null) {
                if ((blockNumber % 1000) == 0) {
                    long timestamp = System.currentTimeMillis();
                    System.out.println((timestamp - start) + ", " + blockNumber);
                }

                blockStorage.storeBlock(blockNumber, block);

                if (debug) {
                    Block fromDb = blockStorage.getBlock(blockNumber);

                    Assert.assertEquals(block, fromDb);
                }

                block = blockChain.next();
                blockNumber++;
            }
        } finally {
            long timestamp = System.currentTimeMillis();
            long duration = timestamp - start;
            System.out.println(duration + " ms for " + blockNumber + " block(s)");

            double averageDurationPerBlock = (double) duration / (double) blockNumber;
            System.out.println(averageDurationPerBlock + " ms / block");
        }

        //    BlockHeader blockHeader = block.getBlockHeader();
        //    List<Transaction> transactions = block.getTransactions();
        //    Output firstOutput = transactions.get(0).getOutputs().get(0);

        //    StateMachine stateMachine = injector.getInstance(StateMachine.class);
        //    stateMachine.execute(firstOutput.getScript());
    }
}
