package com.timmattison.cryptocurrency.standard;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/8/13
 * Time: 10:01 AM
 * To change this template use File | Settings | File Templates.
 */
public class StandardMerkleRootCalculator implements MerkleRootCalculator {
    private final static Logger logger = Logger.getLogger(StandardMerkleRootCalculator.class.getName());
    private final HasherFactory hasherFactory;
    private final Comparator<byte[]> comparator;

    @Inject
    public StandardMerkleRootCalculator(HasherFactory hasherFactory, Comparator<byte[]> comparator) {
        this.hasherFactory = hasherFactory;
        this.comparator = comparator;
    }

    @Override
    public byte[] calculateMerkleRoot(List<byte[]> transactionBytes) {
        // Is there only one value?
        if (transactionBytes.size() == 1) {
            // Yes, there is only one value.  Just return it.
            return transactionBytes.get(0);
        }

        //Collections.sort(transactionBytes, comparator);

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

            // Combine the hashed values into the next level of the tree
            for (int loop = 0; loop < (tempTransactionBytes.size() / 2); loop++) {
                try {
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();

                    byte[] firstTransaction = tempTransactionBytes.get(loop * 2);
                    byte[] secondTransaction = tempTransactionBytes.get((loop * 2) + 1);

                    String firstTransactionHex = ByteArrayHelper.toHex(firstTransaction);
                    String secondTransactionHex = ByteArrayHelper.toHex(secondTransaction);

                    logger.fine("First hash : " + firstTransactionHex);
                    logger.fine("Second hash: " + secondTransactionHex);

                    baos.write(firstTransaction);
                    baos.write(secondTransaction);

                    byte[] result = hasherFactory.createHasher(baos.toByteArray()).getOutput();

                    String resultHex = ByteArrayHelper.toHex(result);
                    logger.fine("Result hash:" + resultHex);

                    transactionBytes.add(result);
                } catch (IOException e) {
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
}
