package com.timmattison.cryptocurrency.standard.hashing.chunks;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 6/23/13
 * Time: 11:19 PM
 * To change this template use File | Settings | File Templates.
 */
public interface ChunkExtractor {
    List<byte[]> getChunks(byte[] input, int bitLengthWithPadding, int modulus);
}
