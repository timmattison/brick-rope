package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinProcessBlockChain {
    public static void main(String[] args) throws FileNotFoundException {
        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChain.class);

        File inputFile = new File("bitcoin-blockchain.dat");
        FileInputStream inputStream = new FileInputStream(inputFile);

        blockChain.setInputStream(inputStream);

        Block ninthBlock = null;
        Block oneHundredAndSeventythBlock = null;

        for (int loop = 0; loop < 99999; loop++) {
            Block block = blockChain.next();

            if (loop == 9) {
                ninthBlock = block;
            } else if (loop == 170) {
                oneHundredAndSeventythBlock = block;
            }
        }

        //    BlockHeader blockHeader = block.getBlockHeader();
        //    List<Transaction> transactions = block.getTransactions();
        //    Output firstOutput = transactions.get(0).getOutputs().get(0);

        //    StateMachine stateMachine = injector.getInstance(StateMachine.class);
        //    stateMachine.execute(firstOutput.getScript());
    }
}
