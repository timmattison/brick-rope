package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.standard.Script;
import com.timmattison.cryptocurrency.standard.ValidationScript;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinValidateAllBlocks {
    public static ValidationScript validationScript;

    public static void main(String[] args) throws FileNotFoundException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        StateMachineFactory stateMachineFactory = injector.getInstance(StateMachineFactory.class);
        ScriptingFactory scriptingFactory = injector.getInstance(ScriptingFactory.class);

        Block block;
        int blockNumber = -1;

        while ((block = blockChain.next()) != null) {
            blockNumber++;

            // Does this block have any non-coinbase transactions?
            if (block.getTransactions().size() == 1) {
                // No, just continue
                continue;
            }

            for (int loop = 1; loop < block.getTransactions().size(); loop++) {
                // Loop through the non-coinbase transactions and prove they are valid
                System.out.println(block.prettyDump(0));
                System.out.println(ByteArrayHelper.toHex(block.dump()));

                System.out.println("Transaction bytes:");
                System.out.println(ByteArrayHelper.toHex(block.getTransactions().get(0).dump()));
                System.out.println("Output bytes:");
                System.out.println(ByteArrayHelper.toHex(block.getTransactions().get(0).getOutputs().get(0).dump()));
                // This is the first block that has more than one transaction.

                Transaction currentTransaction = block.getTransactions().get(loop);

                // Get its inputs
                List<Input> inputs = currentTransaction.getInputs();

                // Validate each input
                for (Input input : inputs) {
                    long previousOutputIndex = input.getPreviousOutputIndex();

                    // Get the previous transaction
                    Transaction previousTransaction = transactionLocator.findTransaction(input.getPreviousTransactionId());

                    // Get the output
                    Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

                    // Get the input script
                    Script inputScript = input.getScript();

                    // Get the output script
                    Script outputScript = previousOutput.getScript();

                    validationScript = scriptingFactory.createValidationScript(inputScript, outputScript);

                    StateMachine stateMachine = stateMachineFactory.createStateMachine();
                    stateMachine.setPreviousTransactionHash(input.getPreviousTransactionId());
                    stateMachine.setCurrentTransactionHash(block.getTransactions().get(loop).getHash());

                    stateMachine.execute(validationScript);
                }
            }
        }
    }
}
