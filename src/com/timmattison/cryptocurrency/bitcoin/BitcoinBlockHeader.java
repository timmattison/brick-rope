package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.factories.HasherFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.interfaces.BlockHeader;
import com.timmattison.cryptocurrency.interfaces.Hash;
import com.timmattison.cryptocurrency.interfaces.Target;
import com.timmattison.cryptocurrency.interfaces.TargetFactory;

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
    private final TargetFactory targetFactory;
    /**
     * The target.  This is not in the block chain.  It is derived from the difficulty.
     */
    private BigInteger targetBigInteger;
    private Target target;
    /**
     * This is the hash of the header.  This is not in the block chain.  It is derived from the double SHA256 hash of the block header bytes.
     */
    private Hash hash;
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
    public BitcoinBlockHeader(HasherFactory hasherFactory, TargetFactory targetFactory) {
        this.hasherFactory = hasherFactory;
        this.targetFactory = targetFactory;
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
    public byte[] dump() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(versionBytes);
            baos.write(prevBlock);
            baos.write(merkleRoot);
            baos.write(timestampBytes);
            baos.write(bitsBytes);
            baos.write(nonceBytes);

            return baos.toByteArray();
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    @Override
    public String prettyDump(int indentationLevel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        StringBuilder indentation = new StringBuilder();

        for (int loop = 0; loop < indentationLevel; loop++) {
            indentation.append("\t");
        }

        stringBuilder.append(indentation);
        stringBuilder.append("Version: ");
        stringBuilder.append(ByteArrayHelper.toHex(versionBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Previous block hash: ");
        stringBuilder.append(ByteArrayHelper.toHex(prevBlock));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Merkle root: ");
        stringBuilder.append(ByteArrayHelper.toHex(merkleRoot));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Timestamp: ");
        stringBuilder.append(ByteArrayHelper.toHex(timestampBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Bits: ");
        stringBuilder.append(ByteArrayHelper.toHex(bitsBytes));
        stringBuilder.append("\n");

        stringBuilder.append(indentation);
        stringBuilder.append("Nonce: ");
        stringBuilder.append(ByteArrayHelper.toHex(nonceBytes));
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }

    @Override
    public Hash getHash() {
        if (hash == null) {
            hash = hasherFactory.createHasher(dump());
        }

        return hash;
    }

    @Override
    public byte[] getPreviousBlockHash() {
        return prevBlock;
    }

    @Override
    public byte[] getMerkleRoot() {
        return merkleRoot;
    }

    public Target getTarget() {
        if (targetBigInteger == null) {
            // The formula for calculating target is:
            // Difficulty: 0x1d00ffff
            // Target: 0x00ffff * (2 ** (8 * (0x1d - 3)))
            // Formula = (Last three bytes of difficulty) * (2 ** (8 * ((First byte of difficulty) - 3)))
            BigInteger lastThreeBytesOfDifficulty = new BigInteger(String.valueOf((long) bits & 0x00FFFFFFL));
            int firstByteOfDifficulty = (int) ((bits & 0xFF000000L) >>> 24);
            BigInteger two = new BigInteger("2");

            targetBigInteger = lastThreeBytesOfDifficulty.multiply(two.pow((8 * (firstByteOfDifficulty - 3))));
        }

        if (target == null) {
            target = targetFactory.create(targetBigInteger);
        }

        return target;
    }

    @Override
    public BigInteger getHashBigInteger() {
        return getHash().getOutputBigInteger();
    }

    public int getDifficulty() {
        return bits;
    }
}
