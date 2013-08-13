package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.factories.ScriptFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinValidateBlock170 {
    public static void main(String[] args) throws FileNotFoundException {
        List<Block> blocks = new ArrayList<Block>();
        Map<String, Transaction> transactionMap = new HashMap<String, Transaction>();

        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChain.class);

        File inputFile = new File("bitcoin-blockchain.dat");
        FileInputStream inputStream = new FileInputStream(inputFile);

        blockChain.setInputStream(inputStream);

        Block block = blockChain.next();
        int blockNumber = 0;

        long start = new Date().getTime();

        while (block != null) {
            blocks.add(block);

            for(Transaction transaction : block.getTransactions()) {
                String hash = ByteArrayHelper.toHex(transaction.getHash());
                transactionMap.put(hash, transaction);
            }

            if (blockNumber == 170) {
                // This is the first block that has more than one transaction.

                // Get the second transaction
                Transaction secondTransactionBlock170 = block.getTransactions().get(1);

                // Get its inputs
                List<Input> inputs = secondTransactionBlock170.getInputs();

                Input input = inputs.get(0);
                String previousTransactionHash = ByteArrayHelper.toHex(input.getPreviousTransactionId());

                // Does this transaction exist?
                if(!transactionMap.containsKey(previousTransactionHash)) {
                    // No, this is bad
                    throw new UnsupportedOperationException("Couldn't find transaction");
                }

                long previousOutputIndex = input.getPreviousOutputIndex();

                // Get the previous transaction
                Transaction previousTransaction = transactionMap.get(previousTransactionHash);

                // Get the output
                Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

                // Get the input script
                Script inputScript = input.getScript();
                List<Word> inputWordList = inputScript.getWords();

                // Get the output script
                Script outputScript = previousOutput.getScript();
                List<Word> outputWordList = outputScript.getWords();

                ValidationScript validationScript = injector.getInstance(ScriptFactory.class).createValidationScript(inputScript, outputScript);

                StateMachine stateMachine = injector.getInstance(StateMachineFactory.class).createStateMachine();

                stateMachine.execute(validationScript);

                return;
            }

            block = blockChain.next();
            blockNumber++;
        }
    }
}
