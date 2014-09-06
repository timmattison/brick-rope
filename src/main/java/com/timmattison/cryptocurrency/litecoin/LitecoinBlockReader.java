package com.timmattison.cryptocurrency.litecoin;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.helpers.EndiannessHelper;
import com.timmattison.cryptocurrency.helpers.InputStreamHelper;
import com.timmattison.cryptocurrency.interfaces.BlockReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class LitecoinBlockReader implements BlockReader {
    // XXX - Duplicated from other classes, this should be shared somehow!
    private static final byte[] requiredMagicNumberBytes = new byte[]{(byte) 0xfb, (byte) 0xc0, (byte) 0xb6, (byte) 0xdb};
    private static final int magicNumberLengthInBytes = 4;
    private static final int blockSizeLengthInBytes = 4;
    private static final int bytesRequired = magicNumberLengthInBytes + blockSizeLengthInBytes;

    public LitecoinBlockReader() {
    }

    @Override
    public byte[] getNextBlock(InputStream inputStream) throws IOException {
        // Are there enough bytes to read the values we need?
        if (InputStreamHelper.getAvailableBytes(inputStream) < bytesRequired) {
            // No, just return NULL
            return null;
        }

        // Get the magic number bytes
        byte[] magicNumber = new byte[magicNumberLengthInBytes];
        int bytesRead = inputStream.read(magicNumber, 0, magicNumberLengthInBytes);

        // Did we get the right number of bytes?
        if (bytesRead != magicNumberLengthInBytes) {
            // No, don't be clever and just throw an exception
            throwExceptionWhenIncorrectLengthRead("the magic number", magicNumberLengthInBytes, bytesRead);
        }

        // Did we get the value we expected?
        if (!Arrays.equals(requiredMagicNumberBytes, magicNumber)) {
            // No, throw an exception
            throwExceptionWhenIncorrectValueRead("the magic number", requiredMagicNumberBytes, magicNumber);
        }

        // Read the block size
        byte[] blockSizeBytes = new byte[blockSizeLengthInBytes];
        bytesRead = inputStream.read(blockSizeBytes, 0, blockSizeLengthInBytes);

        // Did we get the right number of bytes?
        if (bytesRead != blockSizeLengthInBytes) {
            // No, don't be clever and just throw an exception
            throwExceptionWhenIncorrectLengthRead("the block size", blockSizeLengthInBytes, bytesRead);
        }

        // Convert the block size to a value we can use
        int blockSize = EndiannessHelper.BytesToInt(blockSizeBytes);

        // Was the block size valid?
        if (blockSize == 0) {
            // No, don't be clever and just throw an exception
            throw new IllegalStateException("Block size cannot be zero");
        }

        // Read the rest of the block data
        byte[] blockData = new byte[blockSize];
        bytesRead = inputStream.read(blockData, 0, blockSize);

        if (blockSize != bytesRead) {
            throwExceptionWhenIncorrectLengthRead("the block data", magicNumberLengthInBytes, bytesRead);
        }

        return blockData;
    }

    @Override
    public void skipNextBlocks(InputStream inputStream, int count) throws IOException {
        for (int loop = 0; loop < count; loop++) {
            getNextBlock(inputStream);
        }
    }

    private void throwExceptionWhenIncorrectLengthRead(String name, int expected, int read) {
        throw new IllegalStateException("Needed to read " + expected + " byte(s), only read " + read + " byte(s) for " + name);
    }

    private void throwExceptionWhenIncorrectValueRead(String name, byte[] expected, byte[] read) {
        throw new IllegalStateException("Needed to read " + ByteArrayHelper.toHex(expected) + " but read " + ByteArrayHelper.toHex(read) + " for " + name);
    }
}
