package com.timmattison.cryptocurrency.standard;

import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class ParallelMerkleRootCalculator implements MerkleRootCalculator {
    private final static Logger logger = Logger.getLogger(ParallelMerkleRootCalculator.class.getName());
    private final HasherFactory hasherFactory;

    @Inject
    public ParallelMerkleRootCalculator(HasherFactory hasherFactory) {
        this.hasherFactory = hasherFactory;
    }

    @Override
    public byte[] calculateMerkleRoot(List<byte[]> transactionBytes) {
        // Is there only one value?
        if (transactionBytes.size() == 1) {
            // Yes, there is only one value.  Just return it.
            return transactionBytes.get(0);
        }

        // Keep looping and hashing until there is only one value
        do {
            // Is the number of transactions odd?
            if ((transactionBytes.size() % 2) == 1) {
                // Yes, duplicate the last transaction getHash
                transactionBytes.add(transactionBytes.get(transactionBytes.size() - 1));
            }

            // Copy the original data
            List<byte[]> tempTransactionBytes = new ArrayList<byte[]>(transactionBytes);

            // Clear out the original data
            transactionBytes = new ArrayList<byte[]>();

            // Create an array of threads
            int nodes = tempTransactionBytes.size() / 2;
            HashThread[] hashThreads = new HashThread[nodes];

            // Combine the hashed values into the next level of the tree
            for (int loop = 0; loop < nodes; loop++) {
                byte[] firstTransaction = tempTransactionBytes.get(loop * 2);
                byte[] secondTransaction = tempTransactionBytes.get((loop * 2) + 1);

                HashThread hashThread = new HashThread(firstTransaction, secondTransaction);
                hashThreads[loop] = hashThread;
                hashThreads[loop].start();
            }

            for (int loop = 0; loop < hashThreads.length; loop++) {
                try {
                    hashThreads[loop].join();

                    transactionBytes.add(hashThreads[loop].getOutput());
                } catch (InterruptedException e) {
                    throw new UnsupportedOperationException(e);
                }
            }
        } while (transactionBytes.size() != 1);

        return transactionBytes.get(0);
    }

    @Override
    public byte[] calculateMerkleRoot(Block block) {
        List<byte[]> transactionBytes = new ArrayList<byte[]>();

        logger.fine("Number of transactions used in merkle root calculation: " + block.getTransactions().size());

        for (Transaction transaction : block.getTransactions()) {
            transactionBytes.add(hasherFactory.createHasher(transaction.dump()).getOutput());
        }

        return calculateMerkleRoot(transactionBytes);
    }

    private class HashThread extends Thread {
        final byte[] first;
        final byte[] second;
        private byte[] result;

        public HashThread(byte[] first, byte[] second) {
            this.first = first;
            this.second = second;
        }

        @Override
        public void run() {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                baos.write(first);
                baos.write(second);

                result = hasherFactory.createHasher(baos.toByteArray()).getOutput();
            } catch (IOException e) {
                throw new UnsupportedOperationException(e);
            }
        }

        public byte[] getOutput() {
            return result;
        }
    }
}
