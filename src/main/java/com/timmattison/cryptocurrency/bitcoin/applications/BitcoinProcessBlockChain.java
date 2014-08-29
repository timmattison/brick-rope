package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.BlockStorageFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
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

        if (options.containsKey(ApplicationHelper.DATABASE)) {
            bitcoinModule.useH2Storage(options.get(ApplicationHelper.DATABASE));
        }

        if (options.containsKey(ApplicationHelper.BLOCKCHAIN)) {
            bitcoinModule.useBlockChainFile(options.get(ApplicationHelper.BLOCKCHAIN));
        }

        Injector injector = Guice.createInjector(bitcoinModule);
        logger = injector.getInstance(Logger.class);

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

            // TODO: Do something cool with the block
            //Block fromDb = blockStorage.getBlock(blockNumber);

            block = blockChain.next();
            blockNumber++;
        }

        //    BlockHeader blockHeader = block.getBlockHeader();
        //    List<Transaction> transactions = block.getTransactions();
        //    Output firstOutput = transactions.get(0).getOutputs().get(0);

        //    StateMachine stateMachine = injector.getInstance(StateMachine.class);
        //    stateMachine.execute(firstOutput.getScript());
    }
}
