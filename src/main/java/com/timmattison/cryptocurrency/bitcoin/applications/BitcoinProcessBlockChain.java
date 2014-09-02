package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.*;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
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

    public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException, ParseException {
        ApplicationHelper.logFine();

        Map<String, String> options = ApplicationHelper.processCommandLineOptions(args, APPLICATION_NAME);

        BitcoinModule bitcoinModule = new BitcoinModule();

//        if (options.containsKey(ApplicationHelper.DATABASE)) {
//            bitcoinModule.useH2Storage(options.get(ApplicationHelper.DATABASE));
//        }

        if (options.containsKey(ApplicationHelper.DATABASE)) {
            bitcoinModule.usePostgresqlStorage(options.get(ApplicationHelper.DATABASE));
        }

        if (options.containsKey(ApplicationHelper.BLOCKCHAIN)) {
            bitcoinModule.useBlockChainFile(options.get(ApplicationHelper.BLOCKCHAIN));
        }

        Injector injector = Guice.createInjector(bitcoinModule);
        logger = injector.getInstance(Logger.class);

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain();
        BlockStorage blockStorage = injector.getInstance(BlockStorage.class);
        BlockValidator blockValidator = injector.getInstance(BlockValidator.class);

        long blockNumber = 0;

        int blockCount = blockStorage.getBlockCount();

        if (blockCount > 0) {
            long lastBlockNumber = blockStorage.getLastBlockNumber();
            long lastBlockOffset = blockStorage.getBlockOffset(lastBlockNumber);

            // Go to the last block we already processed
            blockChain.skip(lastBlockOffset);

            // Read the block so the next read skips over it
            Block lastBlock = blockChain.next();

            if(!blockValidator.isValid(lastBlock, lastBlockNumber)) {
                throw new UnsupportedOperationException("Block failed to validate!");
            }

            blockNumber = lastBlockNumber + 1;

            logger.info("Starting at block number " + blockNumber + ", offset " + lastBlockOffset);
        }

        Block block = blockChain.next();

        while (block != null) {
            blockStorage.storeBlock(blockNumber, block);

            Block fromDb = blockStorage.getBlock(blockNumber);

            if(!blockValidator.isValid(fromDb, blockNumber)) {
                throw new UnsupportedOperationException("Block failed to validate!");
            }

            // Move onto the next block
            block = blockChain.next();
            blockNumber++;
        }
    }

    private static void validateTransactions(TransactionValidator transactionValidator, Block block, long blockNumber) throws SQLException, IOException, ClassNotFoundException {
        // Get the transaction list
        List<Transaction> transactionList = block.getTransactions();

        // Does the block have any transactions other than the coinbase?
        if (transactionList.size() > 1) {
            // Yes, check them out
            logger.info((transactionList.size() - 1) + " transaction(s) other than the coinbase in block number " + blockNumber);
        } else {
            // No, do nothing
            //logger.info("Only a coinbase in block number " + blockNumber);
        }

        for (Transaction currentTransaction : transactionList) {
            if (currentTransaction.getTransactionNumber() == 0) {
                continue;
            }

            // Validate the transaction
            if (!transactionValidator.isValid(currentTransaction)) {
                throw new UnsupportedOperationException("Transactions in block number " + blockNumber + " are not valid");
            }
        }
    }
}
