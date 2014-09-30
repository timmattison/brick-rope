package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;
import com.timmattison.cryptocurrency.modules.BitcoinModule;
import com.timmattison.cryptocurrency.standard.interfaces.BlockStorage;
import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.sql.SQLException;
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
        BlockValidator blockValidator = injector.getInstance(BlockValidator.class);
        TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);

        long blockNumber = 0;

        int blockCount = blockStorage.getBlockCount();

        if (blockCount > 0) {
            long lastBlockNumber = blockStorage.getLastBlockNumber();
            long lastBlockOffset = blockStorage.getBlockOffset(lastBlockNumber);

            // Go to the last block we already processed
            blockChain.skip(lastBlockOffset);

            // Read the block so the next read skips over it
            Block lastBlock = blockChain.next();

            if (!blockValidator.isValid(lastBlock, lastBlockNumber)) {
                throw new UnsupportedOperationException("Block failed to validate!");
            }

            blockNumber = lastBlockNumber + 1;

            logger.info("Starting at block number " + blockNumber + ", offset " + lastBlockOffset);
        }

        Block block = blockChain.next();

        while (block != null) {
            // Add this block as a temporary block so that we can validate the transactions in it
            transactionLocator.addTemporaryBlock(block);

            // Is this block valid?
            if (!blockValidator.isValid(block, blockNumber)) {
                // No, throw an exception
                throw new UnsupportedOperationException("Block failed to validate!");
            }

            // The block was valid, store it
            blockStorage.storeBlock(blockNumber, block);

            // Move onto the next block
            block = blockChain.next();
            blockNumber++;
        }
    }
}
