package com.timmattison.cryptocurrency.bitcoin.applications;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockChainIndex;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.factories.BlockChainFactory;
import com.timmattison.cryptocurrency.factories.StateMachineFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockChain;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:58 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBuildBlockIndex {
    private static int chunkSize = 10000;

    public static void main(String[] args) throws IOException {
        ApplicationHelper.logFine();

        Injector injector = Guice.createInjector(new BitcoinModule());

        BlockChain blockChain = injector.getInstance(BlockChainFactory.class).getBlockChain("bitcoin-blockchain.dat");
        TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        StateMachineFactory stateMachineFactory = injector.getInstance(StateMachineFactory.class);

        Block block;
        int blockNumber = -1;
        int indexNumber = 0;

        BitcoinBlockChainIndex bitcoinBlockChainIndex = new BitcoinBlockChainIndex();

        while ((block = blockChain.next()) != null) {
            blockNumber++;

            for (int loop = 0; loop < block.getTransactions().size(); loop++) {
                // Loop through the non-coinbase transactions and prove they are valid
                Transaction currentTransaction = block.getTransactions().get(loop);
                byte[] currentTransactionHash = currentTransaction.getHash();

                // Put the index data into the structure
                bitcoinBlockChainIndex.getBlockOffsets().put(blockNumber, Long.valueOf(-1));
                bitcoinBlockChainIndex.getTransactionBlockLocation().put(currentTransactionHash, blockNumber);
            }

            // Are we at a block boundary?
            if ((blockNumber % chunkSize) == 0) {
                // Yes, write the index and keep going.
                indexNumber = writeIndex(indexNumber, bitcoinBlockChainIndex);

                // Create a new index
                bitcoinBlockChainIndex = new BitcoinBlockChainIndex();
            }
        }

        // Is there any more data to write?
        if (bitcoinBlockChainIndex.getBlockRangeEnd() != null) {
            // Yes, write it
            writeIndex(indexNumber, bitcoinBlockChainIndex);
        }
    }

    private static int writeIndex(int indexNumber, BitcoinBlockChainIndex bitcoinBlockChainIndex) throws IOException {
        // Write it
        FileOutputStream fileOutputStream = new FileOutputStream("blockchain.idx." + indexNumber);
        new ObjectOutputStream(fileOutputStream).writeObject(bitcoinBlockChainIndex);
        fileOutputStream.close();

        // Move to the next index
        indexNumber++;

        return indexNumber;
    }
}
