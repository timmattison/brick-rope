package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/1/13
 * Time: 6:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlockHeader implements BlockHeader {
    private static final int versionLengthInBytes = 4;
    private static final int prevBlockLengthInBytes = 32;
    private static final int merkleRootLengthInBytes = 32;
    private static final int timestampLengthInBytes = 4;
    private static final int bitsLengthInBytes = 4;
    private static final int nonceLengthInBytes = 4;
    private final byte[] data;

    /**
     * The target.  This is not in the block chain.  It is derived from the difficulty.
     */
    private BigInteger targetBigInteger;
    private byte[] targetBytes;

    /**
     * This is the hash of the header.  This is not in the block chain.  It is derived from the double SHA256 hash of the block header bytes.
     */
    private byte[] hashBytes;
    private BigInteger hashBigInteger;

    /**
     * The software version that created this block
     */
    private int version;
    private byte[] versionBytes;
    /**
     * The hash value of the previous block this particular block references
     */
    private byte[] prevBlock;
    /**
     * The reference to a Merkle tree collection which is a hash of all transactions related to this block
     */
    private byte[] merkleRoot;
    /**
     * A Unix timestamp recording when this block was created (currently limited to dates before the year 2106!)
     */
    private int timestamp;
    private byte[] timestampBytes;
    /**
     * The calculated difficulty target being used for this block
     */
    private int bits;
    private byte[] bitsBytes;
    /**
     * The nonce used to generate this block... to allow variations of the header and compute different hashes
     */
    private int nonce;
    private byte[] nonceBytes;

    public BitcoinBlockHeader(byte[] data) {
        this.data = data;
    }

    @Override
    public byte[] build() {
        int position = 0;

        // Get the version
        versionBytes = Arrays.copyOfRange(data, position, position + versionLengthInBytes);
        position += versionLengthInBytes;
        version = EndiannessHelper.BytesToInt(versionBytes);

        // Get the previous block hash
        prevBlock = Arrays.copyOfRange(data, position, position + prevBlockLengthInBytes);
        position += prevBlockLengthInBytes;

        // Get the Merkle root
        merkleRoot = Arrays.copyOfRange(data, position, position + merkleRootLengthInBytes);
        position += merkleRootLengthInBytes;

        // Get the timestamp
        timestampBytes = Arrays.copyOfRange(data, position, position + timestampLengthInBytes);
        position += timestampLengthInBytes;
        timestamp = EndiannessHelper.BytesToInt(timestampBytes);

        // Get the difficulty
        bitsBytes = Arrays.copyOfRange(data, position, position + bitsLengthInBytes);
        position += bitsLengthInBytes;
        bits = EndiannessHelper.BytesToInt(bitsBytes);

        // Get the nonce
        nonceBytes = Arrays.copyOfRange(data, position, position + nonceLengthInBytes);
        position += nonceLengthInBytes;
        nonce = EndiannessHelper.BytesToInt(nonceBytes);

        // Return what is left
        return Arrays.copyOfRange(data, position, data.length);
    }
}
