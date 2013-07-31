package com.timmattison.cryptocurrency.interfaces;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 7/29/13
 * Time: 7:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class FileBlockChainStream implements BlockChainStream {
    private final FileInputStream fileInputStream;
    private long offset;

    public FileBlockChainStream(FileInputStream fileInputStream) {
        this.fileInputStream = fileInputStream;
    }

    @Override
    public long bytesLeft() throws IOException {
        return fileInputStream.available();
    }

    @Override
    public byte readByte() throws IOException {
        byte output = (byte) fileInputStream.read();
        offset++;

        return output;
    }

    @Override
    public byte[] readBytes(int count) throws IOException {
        byte[] output = new byte[count];
        int bytesRead = fileInputStream.read(output, (int) getOffset(), count);

        offset += bytesRead;

        if(bytesRead < count) {
            return Arrays.copyOf(output, bytesRead);
        }
        else {
            return output;
        }
    }

    @Override
    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public long getOffset() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
