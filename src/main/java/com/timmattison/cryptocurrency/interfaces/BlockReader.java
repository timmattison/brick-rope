package com.timmattison.cryptocurrency.interfaces;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockReader {
    public byte[] getNextBlock(InputStream inputStream) throws IOException;

    public void skipNextBlocks(InputStream inputStream, int count) throws IOException;
}
