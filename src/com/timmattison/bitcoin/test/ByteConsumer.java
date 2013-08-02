package com.timmattison.bitcoin.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 9:42 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class ByteConsumer {
    /**
     * This is the number of bytes that are printed out when something goes wrong
     */
    private static final int DEBUG_BYTES = 16;

    /**
     * The bytes as they came from the caller.  Nobody is allowed to touch this.
     */
    protected InputStream inputStream;
    protected Logger logger;

    /**
     * Whether or not the object should print out debug information.  Default is false.
     */
    private boolean debug = false;

    public ByteConsumer(InputStream inputStream) throws IOException {
        this.inputStream = inputStream;
    }

    public ByteConsumer(InputStream inputStream, boolean debug) throws IOException {
        this.inputStream = inputStream;
        setDebug(debug);
    }

    protected void setDebug(boolean debug) {
        this.debug = debug;
    }

    protected boolean isDebug() {
        return debug;
    }

    protected byte[] pullBytes(int count, String reason) throws IOException {
        byte[] bytes = new byte[count];
        inputStream.read(bytes, 0, count);

        if(isDebug()) {
            getLogger().info("Bytes pulled for " + reason + ": " + ByteArrayHelper.formatArray(bytes));
        }

        return bytes;
    }

    protected abstract String getName();

    /**
     * Build the object from the input stream
     */
    protected abstract void build() throws IOException, NoSuchAlgorithmException;

    protected Logger getLogger() {
        if (logger == null) {
            logger = Logger.getLogger(getName());

            try {
                logger.addHandler(BlockChainTest.getHandler());
            } catch (Exception ex) {
                // Do nothing, failed to get a handler
            }
        }

        return logger;
    }

    protected Handler getHandler() throws IOException {
        Handler handler = new FileHandler("test.log");
        handler.setFormatter(new SimpleFormatter());

        return handler;
    }

    protected void pullDebugBytes() {
        try {
            getLogger().info(ByteArrayHelper.formatArray(pullBytes(DEBUG_BYTES, "debug bytes")));
        } catch (IOException e) {
            throw new UnsupportedOperationException("IOException in pullDebugBytes", e);
        }
    }

    /**
     * Dump the object as formatted hex
     * @param pretty is true if the output should be human readable
     */
    protected abstract String dump(boolean pretty);

    /**
     * Dump the raw bytes.  Used for code that needs to hash different parts of a block.
     * @return
     */
    protected abstract byte[] dumpBytes() throws IOException;
}
