package com.timmattison.cryptocurrency.bitcoin;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/20/13
 * Time: 6:53 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockChainIndex implements Serializable {
    /**
     * The first block the index contains (inclusive)
     */
    private Integer blockRangeStart;

    /**
     * The last block the index contains (inclusive)
     */
    private Integer blockRangeEnd;

    /**
     * The offset of a particular block in the block chain
     */
    private Map<Integer, Long> blockOffsets;

    /**
     * Which block contains a particular transaction
     */
    private Map<byte[], Integer> transactionBlockLocation;

    public Integer getBlockRangeStart() {
        if (blockRangeStart == null) {
            validateStructures();
        }

        return blockRangeStart;
    }

    public Integer getBlockRangeEnd() {
        if (blockRangeEnd == null) {
            validateStructures();
        }

        return blockRangeEnd;
    }

    private void validateStructures() {
        // Are there any transactions?
        if ((transactionBlockLocation.size() == 0) && (blockOffsets.size() == 0)) {
            // No, just return
            return;
        }

        // Sanity check: Transaction block location and block offset structure sizes must be non-zero
        if ((transactionBlockLocation.size() == 0) || (blockOffsets.size() == 0)) {
            // This should never happen
            throw new UnsupportedOperationException("One of transaction block locations or block offsets contains no entries but the other structure is not empty");
        }

        int max = Integer.MIN_VALUE;
        int min = Integer.MAX_VALUE;

        // Loop through all of the block numbers and find the min and max
        for (int blockNumber : blockOffsets.keySet()) {
            max = (max < blockNumber) ? blockNumber : max;
            min = (min < blockNumber) ? blockNumber : min;
        }

        // Sanity check: Min and max are either equal or min must be less than max
        if (min > max) {
            throw new UnsupportedOperationException("Min can never be greater than max");
        }

        setBlockRangeStart(min);
        setBlockRangeEnd(max);
    }

    private void setBlockRangeStart(int blockRangeStart) {
        this.blockRangeStart = blockRangeStart;
    }

    private void setBlockRangeEnd(int blockRangeEnd) {
        this.blockRangeEnd = blockRangeEnd;
    }

    public Map<byte[], Integer> getTransactionBlockLocation() {
        if (transactionBlockLocation == null) {
            transactionBlockLocation = new HashMap<byte[], Integer>();
        }

        return transactionBlockLocation;
    }

    public void setTransactionBlockLocation(Map<byte[], Integer> transactionBlockLocation) {
        blockRangeStart = null;
        blockRangeEnd = null;

        this.transactionBlockLocation = transactionBlockLocation;
    }

    public Map<Integer, Long> getBlockOffsets() {
        if (blockOffsets == null) {
            blockOffsets = new HashMap<Integer, Long>();
        }
        return blockOffsets;
    }

    public void setBlockOffsets(Map<Integer, Long> blockOffsets) {
        blockRangeStart = null;
        blockRangeEnd = null;

        this.blockOffsets = blockOffsets;
    }
}
