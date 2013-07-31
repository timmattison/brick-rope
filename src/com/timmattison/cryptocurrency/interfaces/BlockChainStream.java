package com.timmattison.cryptocurrency.interfaces;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:17 AM
 * To change this template use File | Settings | File Templates.
 */
public interface BlockChainStream {
    long bytesLeft() throws IOException;

    byte readByte() throws IOException;

    byte[] readBytes(int count) throws IOException;

    void setOffset(long offset);

    long getOffset();
}
