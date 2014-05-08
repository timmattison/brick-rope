package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;

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
    private static int debugBlock = 2812;

    public static void main(String[] args) throws FileNotFoundException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        StateMachineFactory stateMachineFactory = injector.getInstance(StateMachineFactory.class);
        ScriptingFactory scriptingFactory = injector.getInstance(ScriptingFactory.class);

        Block block;
        int blockNumber = -1;

        StateMachine stateMachine = stateMachineFactory.createStateMachine();

        while ((block = blockChain.next()) != null) {
            System.out.println(block.prettyDump(0));
            blockNumber++;

            // Does this block have any non-coinbase transactions?
            if (block.getTransactions().size() == 1) {
                // No, just continue
                continue;
            }

            for (int loop = 1; loop < block.getTransactions().size(); loop++) {
                // Loop through the non-coinbase transactions and prove they are valid
                Transaction currentTransaction = block.getTransactions().get(loop);

                // Get its inputs
                List<Input> inputs = currentTransaction.getInputs();

                int inputNumber = 0;

                // Validate each input
                for (Input input : inputs) {
                    System.out.println("Block #" + blockNumber + ", input #" + inputNumber);

                    long previousOutputIndex = input.getPreviousOutputIndex();

                    // Get the previous transaction
                    Transaction previousTransaction = transactionLocator.findTransaction(input.getPreviousTransactionId());

                    // Get the output
                    Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

                    // Get the input script
                    Script inputScript = input.getScript();

                    // Get the output script
                    Script outputScript = previousOutput.getScript();

                    // Create the validation script
                    validationScript = scriptingFactory.createValidationScript(inputScript, outputScript);

                    // Create a state machine and give it the references to the transactions by hash
                    stateMachine.setPreviousTransactionHash(input.getPreviousTransactionId());
                    stateMachine.setCurrentTransactionHash(block.getTransactions().get(loop).getHash());
                    stateMachine.setPreviousOutputIndex((int) previousOutputIndex);
                    stateMachine.setInputNumber(inputNumber);

                    // Execute the script.  It will throw an exception if it fails.
                    stateMachine.execute(validationScript);

                    inputNumber++;
                }
            }
        }
    }
}
