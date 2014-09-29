package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.BitcoinInputIterator;
import com.timmattison.cryptocurrency.bitcoin.BitcoinInputProcessor;
import com.timmattison.cryptocurrency.bitcoin.BitcoinScriptClassifier;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.helpers.FutureHelper;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinFindTransactionTypes {
    private static final String APPLICATION_NAME = "BitcoinFindTransactionTypes";
    private static Logger logger;

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        ApplicationHelper.logFine();

        Map<String, String> options = ApplicationHelper.processCommandLineOptions(args, APPLICATION_NAME);

        BitcoinModule bitcoinModule = new BitcoinModule();

        if (options.containsKey(ApplicationHelper.BLOCKCHAIN)) {
            bitcoinModule.useBlockChainFile(options.get(ApplicationHelper.BLOCKCHAIN));
        }

        String database = options.get(ApplicationHelper.DATABASE);
        boolean mysql = Boolean.parseBoolean(options.get(ApplicationHelper.MYSQL));
        boolean postgresql = Boolean.parseBoolean(options.get(ApplicationHelper.POSTGRESQL));

        if (mysql) {
            bitcoinModule.useMySqlStorage(database);
        }

        if (postgresql) {
            bitcoinModule.usePostgresqlStorage(database);
        }

        Injector injector = Guice.createInjector(bitcoinModule);
        logger = injector.getInstance(Logger.class);

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        BlockStorage blockStorage = injector.getInstance(BlockStorage.class);
        BitcoinScriptClassifier bitcoinScriptClassifier = injector.getInstance(BitcoinScriptClassifier.class);
        BitcoinInputIterator bitcoinInputIterator = injector.getInstance(BitcoinInputIterator.class);
        TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        ExecutorService executorService = injector.getInstance(ExecutorService.class);

        long blockNumber = 0;

        int blockCount = blockStorage.getBlockCount();

        if (blockCount > 0) {
            long lastBlockNumber = blockStorage.getLastBlockNumber();
            long lastBlockOffset = blockStorage.getBlockOffset(lastBlockNumber);

            // Go to the last block we already processed
            blockChain.skip(lastBlockOffset);

            // Read the block so the next read skips over it
            Block lastBlock = blockChain.next();

            processInputs(executorService, bitcoinInputIterator, lastBlock, bitcoinScriptClassifier, transactionLocator);

            blockNumber = lastBlockNumber + 1;

            logger.info("Starting at block number " + blockNumber + ", offset " + lastBlockOffset);
        }

        Block block = blockChain.next();

        while (block != null) {
            blockStorage.storeBlock(blockNumber, block);

            Block fromDb = blockStorage.getBlock(blockNumber);

            processInputs(executorService, bitcoinInputIterator, fromDb, bitcoinScriptClassifier, transactionLocator);

            // Move onto the next block
            block = blockChain.next();
            if((blockNumber % 1000) == 0) { logger.info(String.valueOf(blockNumber)); }
            blockNumber++;
        }
    }

    private static void processInputs(ExecutorService executorService, final BitcoinInputIterator bitcoinInputIterator, Block lastBlock, final BitcoinScriptClassifier bitcoinScriptClassifier, final TransactionLocator transactionLocator) {
        List<Future<Boolean>> results = new ArrayList<Future<Boolean>>();

        int transactionNumber = 0;

        for (final Transaction transaction : lastBlock.getTransactions()) {
            final int finalTransactionNumber = transactionNumber;

            final Callable<Boolean> worker = new Callable<Boolean>() {
                @Override
                public Boolean call() throws Exception {
                    bitcoinInputIterator.iterateOverInputs(transaction, new BitcoinInputProcessor() {
                        @Override
                        public void process(Transaction transaction, int inputNumber, Input input) {
                            boolean isCoinbase = ((finalTransactionNumber == 0) && (inputNumber == 0));

                            // Get the index of the previous output that this input is taken from
                            long previousOutputIndex = input.getPreviousOutputIndex();

                            // Get the previous transaction
                            Transaction previousTransaction = transactionLocator.findTransaction(input.getPreviousTransactionId());

                            // Get the previous output
                            Output previousOutput = previousTransaction.getOutputs().get((int) previousOutputIndex);

                            // Get the input script
                            Script inputScript = input.getScript();

                            // Get the previous output script
                            Script outputScript = previousOutput.getScript();

                            bitcoinScriptClassifier.determineScriptType(isCoinbase, inputScript, outputScript);
                        }
                    });
                    return true;
                }
            };

            Future<Boolean> submit = executorService.submit(worker);
            results.add(submit);

            transactionNumber++;
        }

        FutureHelper.allTrue(results);
    }
}
