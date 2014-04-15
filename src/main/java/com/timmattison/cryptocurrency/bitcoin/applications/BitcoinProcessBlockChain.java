package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.standard.BlockStorage;

import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinProcessBlockChain {
    public static void main(String[] args) throws FileNotFoundException, SQLException, ClassNotFoundException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        BlockStorage blockStorage = injector.getInstance(BlockStorage.class);

        Block block = blockChain.next();
        int blockNumber = 0;

        long start = new Date().getTime();

        while (block != null) {
            if((blockNumber % 1000) == 0) {
                long timestamp = new Date().getTime();
                System.out.println((timestamp - start) + ", " + blockNumber);
            }

            blockStorage.storeBlock(blockNumber, block);

            block = blockChain.next();
            blockNumber++;
        }

        //    BlockHeader blockHeader = block.getBlockHeader();
        //    List<Transaction> transactions = block.getTransactions();
        //    Output firstOutput = transactions.get(0).getOutputs().get(0);

        //    StateMachine stateMachine = injector.getInstance(StateMachine.class);
        //    stateMachine.execute(firstOutput.getScript());
    }
}
