package com.timmattison.cryptocurrency.bitcoin;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.interfaces.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class Bitcoin {
    public static void main(String[] args) throws FileNotFoundException {
        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChain.class);

        File inputFile = new File("bitcoin-blockchain.dat");
        FileInputStream inputStream = new FileInputStream(inputFile);

        blockChain.setInputStream(inputStream);
        Block block = blockChain.next();

        BlockHeader blockHeader = block.getBlockHeader();
        List<Transaction> transactions = block.getTransactions();
        Output firstOutput = transactions.get(0).getOutputs().get(0);

        StateMachine stateMachine = injector.getInstance(StateMachine.class);
        stateMachine.execute(firstOutput.getScript());
    }
}
