package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import java.io.FileNotFoundException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinValidateSpecificBlock {
    public static ValidationScript validationScript;
    private static final int blockToValidate = 211914;

    public static void main(String[] args) throws FileNotFoundException {
        List<Block> blocks = new ArrayList<Block>();
        Map<String, Transaction> transactionMap = new HashMap<String, Transaction>();

        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();

        Block block = blockChain.next();
        int blockNumber = 0;

        long start = new Date().getTime();

        while (block != null) {
            blocks.add(block);

            for (Transaction transaction : block.getTransactions()) {
                String hash = ByteArrayHelper.toHex(transaction.getHash());
                transactionMap.put(hash, transaction);
            }

            if (blockNumber == blockToValidate) {
                System.out.println(block.prettyDump(0));
                System.out.println(ByteArrayHelper.toHex(block.dump()));

                System.out.println("Transaction bytes:");
                System.out.println(ByteArrayHelper.toHex(block.getTransactions().get(0).dump()));
                System.out.println("Output bytes:");
                System.out.println(ByteArrayHelper.toHex(block.getTransactions().get(0).getOutputs().get(0).dump()));
                // This is the first block that has more than one transaction.

                Transaction currentTransaction = block.getTransactions().get(1);

                // Get its inputs
                List<Input> inputs = currentTransaction.getInputs();

                Input input = inputs.get(0);
                String previousTransactionHash = ByteArrayHelper.toHex(input.getPreviousTransactionId());

                // Does this transaction exist?
                if (!transactionMap.containsKey(previousTransactionHash)) {
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

                // Get the output script
                Script outputScript = previousOutput.getScript();

                validationScript = injector.getInstance(ScriptingFactory.class).createValidationScript(inputScript, outputScript);

                StateMachine stateMachine = injector.getInstance(StateMachineFactory.class).createStateMachine();
                stateMachine.setPreviousTransactionHash(input.getPreviousTransactionId());
                stateMachine.setCurrentTransactionHash(block.getTransactions().get(1).getHash());

                stateMachine.execute(validationScript);

                return;
            }

            block = blockChain.next();
            blockNumber++;
        }
    }
}
