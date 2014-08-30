package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.BlockStorageFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import com.timmattison.cryptocurrency.standard.interfaces.ValidationScript;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinProcessBlockChain {
    private static final String APPLICATION_NAME = "BitcoinProcessBlockChain";
    private static Logger logger;
    private static ScriptingFactory scriptingFactory;
    private static StateMachineFactory stateMachineFactory;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        ApplicationHelper.logFine();

        Map<String, String> options = ApplicationHelper.processCommandLineOptions(args, APPLICATION_NAME);

        BitcoinModule bitcoinModule = new BitcoinModule();

        if (options.containsKey(ApplicationHelper.DATABASE)) {
            bitcoinModule.useH2Storage(options.get(ApplicationHelper.DATABASE));
        }

        if (options.containsKey(ApplicationHelper.BLOCKCHAIN)) {
            bitcoinModule.useBlockChainFile(options.get(ApplicationHelper.BLOCKCHAIN));
        }

        Injector injector = Guice.createInjector(bitcoinModule);
        logger = injector.getInstance(Logger.class);
        scriptingFactory = injector.getInstance(ScriptingFactory.class);
        stateMachineFactory = injector.getInstance(StateMachineFactory.class);

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        BlockStorage blockStorage = injector.getInstance(BlockStorageFactory.class).getBlockStorage();

        long blockNumber = 0;

        int blockCount = blockStorage.getBlockCount();

        if (blockCount > 0) {
            long lastBlockNumber = blockStorage.getLastBlockNumber();
            long lastBlockOffset = blockStorage.getBlockOffset(lastBlockNumber);

            // Go to the last block we already processed
            blockChain.skip(lastBlockOffset);

            // Read the block so the next read skips over it
            blockChain.next();

            blockNumber = lastBlockNumber + 1;

            logger.info("Starting at block number " + blockNumber + ", offset " + lastBlockOffset);
        }

        Block block = blockChain.next();

        while (block != null) {
            blockStorage.storeBlock(blockNumber, block);

            Block fromDb = blockStorage.getBlock(blockNumber);

            // Get the transaction list
            List<Transaction> transactionList = fromDb.getTransactions();

            // Does the block have any transactions other than the coinbase?
            if (transactionList.size() > 1) {
                // Yes, check them out
                logger.info((transactionList.size() - 1) + " transaction(s) other than the coinbase in block number " + blockNumber);
                checkoutTransactions(blockStorage, scriptingFactory, stateMachineFactory, transactionList);
            } else {
                // No, do nothing
                //logger.info("Only a coinbase in block number " + blockNumber);
            }

            block = blockChain.next();
            blockNumber++;
        }

        //    BlockHeader blockHeader = block.getBlockHeader();
        //    List<Transaction> transactions = block.getTransactions();
        //    Output firstOutput = transactions.get(0).getOutputs().get(0);

        //    StateMachine stateMachine = injector.getInstance(StateMachine.class);
        //    stateMachine.execute(firstOutput.getScript());
    }

    private static void checkoutTransactions(BlockStorage blockStorage, ScriptingFactory scriptingFactory, StateMachineFactory stateMachineFactory, List<Transaction> transactionList) throws SQLException, IOException, ClassNotFoundException {
        for (Transaction currentTransaction : transactionList) {
            if (currentTransaction.getTransactionNumber() == 0) {
                continue;
            }

            // Get its inputs
            List<Input> inputs = currentTransaction.getInputs();

            int inputNumber = 0;

            for (Input input : inputs) {
                long previousOutputIndex = input.getPreviousOutputIndex();

                // Get the previous transaction
                Transaction previousTransaction = blockStorage.getTransaction(ByteArrayHelper.toHex(input.getPreviousTransactionId()));

                // Get the output
                Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

                // Get the input script
                Script inputScript = input.getScript();

                // Get the output script
                Script outputScript = previousOutput.getScript();

                ValidationScript validationScript = scriptingFactory.createValidationScript(inputScript, outputScript);

                StateMachine stateMachine = stateMachineFactory.createStateMachine();
                stateMachine.setPreviousTransactionHash(input.getPreviousTransactionId());
                stateMachine.setPreviousOutputIndex((int) previousOutputIndex);
                stateMachine.setCurrentTransactionHash(currentTransaction.getHash());
                stateMachine.setInputNumber(inputNumber);

                stateMachine.execute(validationScript);

                inputNumber++;
            }
        }
    }
}
