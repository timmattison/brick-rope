package com.timmattison.cryptocurrency.fakes;

import com.timmattison.cryptocurrency.bitcoin.BitcoinBlockReader;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/2/13
 * Time: 6:06 PM
 * To change this template use File | Settings | File Templates.
 */
public class FakeBitcoinBlockReader extends BitcoinBlockReader {
    private final Integer blocksToSkip;
    private boolean skipped = false;

    @Inject
    public FakeBitcoinBlockReader(@Named("blocksToSkip") Integer blocksToSkip) {
        this.blocksToSkip = blocksToSkip;
    }

    @Override
    public byte[] getNextBlock(InputStream inputStream) throws IOException {
        if (!skipped) {
            skipped = true;
            skipNextBlocks(inputStream, blocksToSkip);
        }

        return super.getNextBlock(inputStream);
    }
}
