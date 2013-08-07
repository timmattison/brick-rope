package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private final HasherFactory hasherFactory;
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

    @Inject
    public BitcoinBlockHeader(HasherFactory hasherFactory) {
        this.hasherFactory = hasherFactory;
    }

    @Override
    public byte[] build(byte[] data) {
        // Get the version
        versionBytes = Arrays.copyOfRange(data, 0, versionLengthInBytes);
        data = Arrays.copyOfRange(data, versionLengthInBytes, data.length);
        version = EndiannessHelper.BytesToInt(versionBytes);

        // Get the previous block hash
        prevBlock = Arrays.copyOfRange(data, 0, prevBlockLengthInBytes);
        data = Arrays.copyOfRange(data, prevBlockLengthInBytes, data.length);

        // Get the Merkle root
        merkleRoot = Arrays.copyOfRange(data, 0, merkleRootLengthInBytes);
        data = Arrays.copyOfRange(data, merkleRootLengthInBytes, data.length);

        // Get the timestamp
        timestampBytes = Arrays.copyOfRange(data, 0, timestampLengthInBytes);
        data = Arrays.copyOfRange(data, timestampLengthInBytes, data.length);
        timestamp = EndiannessHelper.BytesToInt(timestampBytes);

        // Get the difficulty
        bitsBytes = Arrays.copyOfRange(data, 0, bitsLengthInBytes);
        data = Arrays.copyOfRange(data, bitsLengthInBytes, data.length);
        bits = EndiannessHelper.BytesToInt(bitsBytes);

        // Get the nonce
        nonceBytes = Arrays.copyOfRange(data, 0, nonceLengthInBytes);
        data = Arrays.copyOfRange(data, nonceLengthInBytes, data.length);
        nonce = EndiannessHelper.BytesToInt(nonceBytes);

        // Return what is left
        return data;
    }

    @Override
    public byte[] hash() {
        if (hashBytes == null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                baos.write(versionBytes);
                baos.write(prevBlock);
                baos.write(merkleRoot);
                baos.write(timestampBytes);
                baos.write(bitsBytes);
                baos.write(nonceBytes);

                hashBytes = hasherFactory.createHasher(baos.toByteArray()).getOutput();
            } catch (IOException e) {
                throw new UnsupportedOperationException(e);
            }
        }

        return hashBytes;
    }

    @Override
    public byte[] getPreviousBlockHash() {
        return prevBlock;
    }
}
