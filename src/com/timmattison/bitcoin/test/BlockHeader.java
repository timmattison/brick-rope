package com.timmattison.bitcoin.test;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 9:32 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlockHeader extends ByteConsumer {
    private static final String name = "BLOCK HEADER";

    private static final String hashName = "SHA-256";

    private static final int versionLengthInBytes = 4;
    private static final int prevBlockLengthInBytes = 32;
    private static final int merkleRootLengthInBytes = 32;
    private static final int timestampLengthInBytes = 4;
    private static final int bitsLengthInBytes = 4;
    private static final int nonceLengthInBytes = 4;

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

    public BlockHeader(InputStream inputStream, boolean debug, boolean innerDebug) throws IOException {
        super(inputStream, debug, innerDebug);
    }

    @Override
    protected String getName() {
        return name;
    }

    @Override
    protected void innerShowDebugInfo() {
        getLogger().info("Version: " + version);
        getLogger().info("Previous block hash: " + ByteArrayHelper.formatArray(prevBlock));
        getLogger().info("Merkle root: " + ByteArrayHelper.formatArray(merkleRoot));
        getLogger().info("Timestamp: " + timestamp);
        getLogger().info("Bits: " + bits);
        getLogger().info("Nonce: " + nonce);
    }

    @Override
    protected void build() throws IOException {
        // Get the version
        versionBytes = pullBytes(versionLengthInBytes, "block header, version");
        version = EndiannessHelper.BytesToInt(versionBytes);

        // Get the previous block hash
        prevBlock = pullBytes(prevBlockLengthInBytes, "block header, prev block");

        // Get the Merkle root
        merkleRoot = pullBytes(merkleRootLengthInBytes, "block header, merkle root");

        // Get the timestamp
        timestampBytes = pullBytes(timestampLengthInBytes, "block header, timestamp");
        timestamp = EndiannessHelper.BytesToInt(timestampBytes);

        // Get the difficulty
        bitsBytes = pullBytes(bitsLengthInBytes, "block header, bits");
        bits = EndiannessHelper.BytesToInt(bitsBytes);

        // Get the nonce
        nonceBytes = pullBytes(nonceLengthInBytes, "block header, nonce");
        nonce = EndiannessHelper.BytesToInt(nonceBytes);
    }

    public byte[] getHash() throws NoSuchAlgorithmException {
        // Get two message digest objects so we can do the two step hash
        MessageDigest hashOfHeader = MessageDigest.getInstance(hashName);
        MessageDigest hashOfHash = MessageDigest.getInstance(hashName);

        // Hash all of the header data
        hashOfHeader.update(EndiannessHelper.IntToBytes(version));
        hashOfHeader.update(prevBlock);
        hashOfHeader.update(merkleRoot);
        hashOfHeader.update(EndiannessHelper.IntToBytes(timestamp));
        hashOfHeader.update(EndiannessHelper.IntToBytes(bits));
        hashOfHeader.update(EndiannessHelper.IntToBytes(nonce));

        // Hash the hashed data
        hashOfHash.update(hashOfHeader.digest());

        // Get the result and print it
        byte[] result = hashOfHash.digest();
        getLogger().info(ByteArrayHelper.formatArray(result));

        // Return it
        return result;
    }

    @Override
    protected String dump(boolean pretty) {
        StringBuilder stringBuilder = new StringBuilder();

        if (pretty) {
            stringBuilder.append("Block header data:\n");
        }

        DumpHelper.dump(stringBuilder, pretty, "\tVersion: ", "\n", versionBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tPrevious block hash: ", "\n", prevBlock);
        DumpHelper.dump(stringBuilder, pretty, "\tMerkle root: ", "\n", merkleRoot);
        DumpHelper.dump(stringBuilder, pretty, "\tTimestamp: ", "\n", timestampBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tBits: ", "\n", bitsBytes);
        DumpHelper.dump(stringBuilder, pretty, "\tNonce: ", "\n", nonceBytes);

        return stringBuilder.toString();
    }
}
