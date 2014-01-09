package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Output;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFindAllUsedOpcodes {
    private static Set<String> opcodes = new HashSet<String>();

    public static void main(String[] args) throws FileNotFoundException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();

        Block block = blockChain.next();
        int blockNumber = 0;

        long start = new Date().getTime();

        try {
            while (block != null) {
                if ((blockNumber % 1000) == 0) {
                    long timestamp = new Date().getTime();
                    System.out.println((timestamp - start) + ", " + blockNumber);

                    System.out.println("  Used so far: " + opcodes.size());

                    for (String opcode : opcodes) {
                        System.out.println("    " + opcode);
                    }

                    System.out.println();
                }

                block = blockChain.next();

                for (Transaction transaction : block.getTransactions()) {
                    // Dump the inputs
                    // XXX - Right now lets just dump the outputs
                    //for(Input input : transaction.getInputs()) {
                    //}

                    for (Output output : transaction.getOutputs()) {
                        for (Word word : output.getScript().getWords()) {
                            opcodes.add(word.getName());
                        }
                    }
                }

                blockNumber++;
            }
        } catch (Exception ex) {
            System.out.println("Exception in block #" + blockNumber + ", [" + ex.getMessage());
        }
    }
}
