package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.HashComparator;
import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.interfaces.Block;
import com.timmattison.cryptocurrency.interfaces.BlockValidator;
import com.timmattison.cryptocurrency.interfaces.MerkleRootCalculator;
import com.timmattison.cryptocurrency.interfaces.Transaction;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 8/7/13
 * Time: 1:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockValidator implements BlockValidator {
    private final HasherFactory hasherFactory;
    private final MerkleRootCalculator merkleRootCalculator;

    @Inject
    public BitcoinBlockValidator(HasherFactory hasherFactory, MerkleRootCalculator merkleRootCalculator) {
        this.hasherFactory = hasherFactory;
        this.merkleRootCalculator = merkleRootCalculator;
    }

    @Override
    public boolean isValid(Block block) {
        byte[] merkleRoot = block.getBlockHeader().getMerkleRoot();
        byte[] calculatedMerkleRoot = calculateMerkleRoot(block);

        return Arrays.equals(block.getBlockHeader().getMerkleRoot(), calculateMerkleRoot(block));
    }

    @Override
    public boolean isParentOf(Block parentBlock, Block childBlock) {
        byte[] hash1 = childBlock.getBlockHeader().getPreviousBlockHash();
        byte[] hash2 = parentBlock.getBlockHeader().getHash();

        return Arrays.equals(childBlock.getBlockHeader().getPreviousBlockHash(), parentBlock.getBlockHeader().getHash());
    }

    private byte[] calculateMerkleRoot(Block block) {
        List<Transaction> transactions = block.getTransactions();

        if ((transactions == null) || (transactions.size() == 0)) {
            throw new UnsupportedOperationException("Cannot calculate the Merkle root with no transactions");
        }

        List<byte[]> transactionBytes = new ArrayList<byte[]>();

        // Build the base of the tree by dumping all of the hashes of the transaction bytes into an array
        for (Transaction transaction : transactions) {
            transactionBytes.add(transaction.hash());
        }

        // Is there only one value?
        if (transactionBytes.size() == 1) {
            // Yes, there is only one value.  Just return it.
            System.out.println("Final transaction hash: " + ByteArrayHelper.toHex(transactionBytes.get(0)));
            return transactionBytes.get(0);
        }

        Collections.sort(transactionBytes, new HashComparator());

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
                    baos.write(tempTransactionBytes.get(loop * 2));
                    baos.write(tempTransactionBytes.get((loop * 2) + 1));
                    transactionBytes.add(hasherFactory.createHasher(baos.toByteArray()).getOutput());
                    System.out.println("Intermediate hash [" + loop + "]: " + ByteArrayHelper.toHex(transactionBytes.get(0)));
                } catch (IOException e) {
                    throw new UnsupportedOperationException(e);
                }
            }
        } while (transactionBytes.size() != 1);

        return transactionBytes.get(0);

    }
}
