package com.timmattison.cryptocurrency.standard.hashing.chunks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 11:20 PM
 * To change this template use File | Settings | File Templates.
 */
public class StandardChunkExtractor implements ChunkExtractor {
    private final int sizeOfChunk = 64;

    @Override
    public List<byte[]> getChunks(byte[] input, int bitLengthWithPadding, int modulus) {
        List<byte[]> chunks = new ArrayList<byte[]>();
        for (int outerLoop = 0; outerLoop < (bitLengthWithPadding / modulus); outerLoop++) {
            byte[] currentChunk = new byte[sizeOfChunk];

            for (int innerLoop = 0; innerLoop < sizeOfChunk; innerLoop++) {
                currentChunk[innerLoop] = input[(outerLoop * sizeOfChunk) + innerLoop];
            }

            chunks.add(currentChunk);
        }

        return chunks;
    }
}
