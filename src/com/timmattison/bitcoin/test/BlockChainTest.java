package com.timmattison.bitcoin.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.SimpleFormatter;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlockChainTest {
    private static final boolean debug = true;
    private static final boolean innerDebug = true;

    public static Handler getHandler() throws IOException {
        Handler handler = new FileHandler("test.log");
        handler.setFormatter(new SimpleFormatter());

        return handler;
    }

    public static void main(String[] args) throws Exception {
        // Did they specify a filename (and only one filename)?
        if ((args == null) || (args.length != 1)) {
            // No, tell them that they need to specify a file
            System.out.println("A filename containing block data must be specified");
            return;
        }

        File inputFile = new File(args[0]);
        InputStream inputStream = new FileInputStream(inputFile);
        BlockChain blockChain = new BlockChain(inputStream, debug, innerDebug);
        blockChain.showDebugInfo();
    }
}
