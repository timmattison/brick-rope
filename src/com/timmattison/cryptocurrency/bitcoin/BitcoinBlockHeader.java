package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;

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
    private final InputStream inputStream;

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

    public BitcoinBlockHeader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void build() {
        try {
            // Get the version
            versionBytes = InputStreamHelper.pullBytes(inputStream, versionLengthInBytes);
            version = EndiannessHelper.BytesToInt(versionBytes);

            // Get the previous block hash
            prevBlock = InputStreamHelper.pullBytes(inputStream, prevBlockLengthInBytes);

            // Get the Merkle root
            merkleRoot = InputStreamHelper.pullBytes(inputStream, merkleRootLengthInBytes);

            // Get the timestamp
            timestampBytes = InputStreamHelper.pullBytes(inputStream, timestampLengthInBytes);
            timestamp = EndiannessHelper.BytesToInt(timestampBytes);

            // Get the difficulty
            bitsBytes = InputStreamHelper.pullBytes(inputStream, bitsLengthInBytes);
            bits = EndiannessHelper.BytesToInt(bitsBytes);

            // Get the nonce
            nonceBytes = InputStreamHelper.pullBytes(inputStream, nonceLengthInBytes);
            nonce = EndiannessHelper.BytesToInt(nonceBytes);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
