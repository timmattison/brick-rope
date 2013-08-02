package com.timmattison.bitcoin.test;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class ScriptTest {
    static Byte[] testScript1 = new Byte[]{
            (byte) 0x76, // OP_DUP
            (byte) 0xA9, // OP_HASH160
            (byte) 0x14, // push 20 bytes
            (byte) 0x89, (byte) 0xAB, (byte) 0xCD, (byte) 0xEF,
            (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA,
            (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA, (byte) 0xAB, (byte) 0xBA,
            (byte) 0x88, // OP_EQUALVERIFY
            (byte) 0xAC  // OP_CHECKSIG
    };

    // scriptsig from the Genesis block - https://en.bitcoin.it/wiki/Genesis_block
    static Byte[] testScript2 = new Byte[]{
            (byte) 0x04, (byte) 0xFF, (byte) 0xFF, (byte) 0x00, (byte) 0x1D, (byte) 0x01, (byte) 0x04, (byte) 0x45,
            (byte) 0x54, (byte) 0x68, (byte) 0x65, (byte) 0x20, (byte) 0x54, (byte) 0x69, (byte) 0x6D, (byte) 0x65,
            (byte) 0x73, (byte) 0x20, (byte) 0x30, (byte) 0x33, (byte) 0x2F, (byte) 0x4A, (byte) 0x61, (byte) 0x6E,
            (byte) 0x2F, (byte) 0x32, (byte) 0x30, (byte) 0x30, (byte) 0x39, (byte) 0x20, (byte) 0x43, (byte) 0x68,
            (byte) 0x61, (byte) 0x6E, (byte) 0x63, (byte) 0x65, (byte) 0x6C, (byte) 0x6C, (byte) 0x6F, (byte) 0x72,
            (byte) 0x20, (byte) 0x6F, (byte) 0x6E, (byte) 0x20, (byte) 0x62, (byte) 0x72, (byte) 0x69, (byte) 0x6E,
            (byte) 0x6B, (byte) 0x20, (byte) 0x6F, (byte) 0x66, (byte) 0x20, (byte) 0x73, (byte) 0x65, (byte) 0x63,
            (byte) 0x6F, (byte) 0x6E, (byte) 0x64, (byte) 0x20, (byte) 0x62, (byte) 0x61, (byte) 0x69, (byte) 0x6C,
            (byte) 0x6F, (byte) 0x75, (byte) 0x74, (byte) 0x20, (byte) 0x66, (byte) 0x6F, (byte) 0x72, (byte) 0x20,
            (byte) 0x62, (byte) 0x61, (byte) 0x6E, (byte) 0x6B, (byte) 0x73
    };

    static Byte[] testScript3 = new Byte[]{
            (byte) 0x01, // push 1 byte
            (byte) 0x61, // "a"
            (byte) 0xa6, // OP_RIPEMD160
            (byte) 0x14, // push 20 bytes
            (byte) 0x0b, (byte) 0xdc, (byte) 0x9d, (byte) 0x2d, (byte) 0x25, (byte) 0x6b, (byte) 0x3e, (byte) 0xe9,
            (byte) 0xda, (byte) 0xae, (byte) 0x34, (byte) 0x7b, (byte) 0xe6, (byte) 0xf4, (byte) 0xdc, (byte) 0x83,
            (byte) 0x5a, (byte) 0x46, (byte) 0x7f, (byte) 0xfe,
            (byte) 0x87  // OP_EQUAL
    };

    private static final boolean debug = true;
    private static final boolean innerDebug = true;

    public static void main(String[] args) throws InstantiationException, IllegalAccessException, IOException {
        //Script script = new Script(testScript3.length, testScript3, debug, innerDebug);
        //Object result = script.execute();
        //System.out.println(result);
    }
}
