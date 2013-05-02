package com.timmattison.bitcoin.test;

import java.io.ByteArrayInputStream;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 8:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BlockTest {
    // Genesis block
    // From: http://james.lab6.com/2012/01/12/bitcoin-285-bytes-that-changed-the-world/
    static byte[] testBlock1 = new byte[]{
            (byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9, // Magic number
            (byte) 0x1d, (byte) 0x01, (byte) 0x00, (byte) 0x00, // Block size
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Block format
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Hash of previous block
            (byte) 0x3b, (byte) 0xa3, (byte) 0xed, (byte) 0xfd,
            (byte) 0x7a, (byte) 0x7b, (byte) 0x12, (byte) 0xb2,
            (byte) 0x7a, (byte) 0xc7, (byte) 0x2c, (byte) 0x3e,
            (byte) 0x67, (byte) 0x76, (byte) 0x8f, (byte) 0x61,
            (byte) 0x7f, (byte) 0xc8, (byte) 0x1b, (byte) 0xc3,
            (byte) 0x88, (byte) 0x8a, (byte) 0x51, (byte) 0x32,
            (byte) 0x3a, (byte) 0x9f, (byte) 0xb8, (byte) 0xaa,
            (byte) 0x4b, (byte) 0x1e, (byte) 0x5e, (byte) 0x4a, // Merkle root
            (byte) 0x29, (byte) 0xab, (byte) 0x5f, (byte) 0x49, // Timestamp
            (byte) 0xff, (byte) 0xff, (byte) 0x00, (byte) 0x1d, // Bits
            (byte) 0x1d, (byte) 0xac, (byte) 0x2b, (byte) 0x7c, // Nonce
            (byte) 0x01,                                        // Transaction count

            // First transaction
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Transaction version number
            (byte) 0x01,                                        // Input count
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Previous transaction hash (the "to-be-used transaction")
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, // Index of output of the to-be-used transaction
            (byte) 0x4d,                                        // Script length
            (byte) 0x04, (byte) 0xff, (byte) 0xff, (byte) 0x00,
            (byte) 0x1d, (byte) 0x01, (byte) 0x04, (byte) 0x45,
            (byte) 0x54, (byte) 0x68, (byte) 0x65, (byte) 0x20,
            (byte) 0x54, (byte) 0x69, (byte) 0x6d, (byte) 0x65,
            (byte) 0x73, (byte) 0x20, (byte) 0x30, (byte) 0x33,
            (byte) 0x2f, (byte) 0x4a, (byte) 0x61, (byte) 0x6e,
            (byte) 0x2f, (byte) 0x32, (byte) 0x30, (byte) 0x30,
            (byte) 0x39, (byte) 0x20, (byte) 0x43, (byte) 0x68,
            (byte) 0x61, (byte) 0x6e, (byte) 0x63, (byte) 0x65,
            (byte) 0x6c, (byte) 0x6c, (byte) 0x6f, (byte) 0x72,
            (byte) 0x20, (byte) 0x6f, (byte) 0x6e, (byte) 0x20,
            (byte) 0x62, (byte) 0x72, (byte) 0x69, (byte) 0x6e,
            (byte) 0x6b, (byte) 0x20, (byte) 0x6f, (byte) 0x66,
            (byte) 0x20, (byte) 0x73, (byte) 0x65, (byte) 0x63,
            (byte) 0x6f, (byte) 0x6e, (byte) 0x64, (byte) 0x20,
            (byte) 0x62, (byte) 0x61, (byte) 0x69, (byte) 0x6c,
            (byte) 0x6f, (byte) 0x75, (byte) 0x74, (byte) 0x20,
            (byte) 0x66, (byte) 0x6f, (byte) 0x72, (byte) 0x20,
            (byte) 0x62, (byte) 0x61, (byte) 0x6e, (byte) 0x6b,
            (byte) 0x73,                                        // Script
            (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, // Sequence number
            (byte) 0x01,                                        // Output count
            (byte) 0x00, (byte) 0xf2, (byte) 0x05, (byte) 0x2a,
            (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, // Output value
            (byte) 0x43,                                        // Script length
            (byte) 0x41, (byte) 0x04, (byte) 0x67, (byte) 0x8a,
            (byte) 0xfd, (byte) 0xb0, (byte) 0xfe, (byte) 0x55,
            (byte) 0x48, (byte) 0x27, (byte) 0x19, (byte) 0x67,
            (byte) 0xf1, (byte) 0xa6, (byte) 0x71, (byte) 0x30,
            (byte) 0xb7, (byte) 0x10, (byte) 0x5c, (byte) 0xd6,
            (byte) 0xa8, (byte) 0x28, (byte) 0xe0, (byte) 0x39,
            (byte) 0x09, (byte) 0xa6, (byte) 0x79, (byte) 0x62,
            (byte) 0xe0, (byte) 0xea, (byte) 0x1f, (byte) 0x61,
            (byte) 0xde, (byte) 0xb6, (byte) 0x49, (byte) 0xf6,
            (byte) 0xbc, (byte) 0x3f, (byte) 0x4c, (byte) 0xef,
            (byte) 0x38, (byte) 0xc4, (byte) 0xf3, (byte) 0x55,
            (byte) 0x04, (byte) 0xe5, (byte) 0x1e, (byte) 0xc1,
            (byte) 0x12, (byte) 0xde, (byte) 0x5c, (byte) 0x38,
            (byte) 0x4d, (byte) 0xf7, (byte) 0xba, (byte) 0x0b,
            (byte) 0x8d, (byte) 0x57, (byte) 0x8a, (byte) 0x4c,
            (byte) 0x70, (byte) 0x2b, (byte) 0x6b, (byte) 0xf1,
            (byte) 0x1d, (byte) 0x5f, (byte) 0xac,              // Script
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00  // Lock time
    };
    static ByteArrayInputStream testBlock1InputStream = new ByteArrayInputStream(testBlock1);

    static String testBlock2Data = "F9 BE B4 D9 D7 00 00 00 01 00 00 00 6F E2 8C 0A" +
            "B6 F1 B3 72 C1 A6 A2 46 AE 63 F7 4F 93 1E 83 65" +
            "E1 5A 08 9C 68 D6 19 00 00 00 00 00 98 20 51 FD" +
            "1E 4B A7 44 BB BE 68 0E 1F EE 14 67 7B A1 A3 C3" +
            "54 0B F7 B1 CD B6 06 E8 57 23 3E 0E 61 BC 66 49" +
            "FF FF 00 1D 01 E3 62 99 01 01 00 00 00 01 00 00" +
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 00 00" +
            "00 00 00 00 00 00 00 00 00 00 00 00 00 00 FF FF" +
            "FF FF 07 04 FF FF 00 1D 01 04 FF FF FF FF 01 00" +
            "F2 05 2A 01 00 00 00 43 41 04 96 B5 38 E8 53 51" +
            "9C 72 6A 2C 91 E6 1E C1 16 00 AE 13 90 81 3A 62" +
            "7C 66 FB 8B E7 94 7B E6 3C 52 DA 75 89 37 95 15" +
            "D4 E0 A6 04 F8 14 17 81 E6 22 94 72 11 66 BF 62" +
            "1E 73 A8 2C BF 23 42 C8 58 EE AC 00 00 00 00";

    static byte[] testBlock2 = ImportFromText.Import(testBlock2Data);
    static ByteArrayInputStream testBlock2InputStream = new ByteArrayInputStream(testBlock2);

    private static final boolean debug = true;
    private static final boolean innerDebug = true;

    public static void main(String[] args) throws Exception {
        Block block1 = new Block(testBlock1InputStream, 1, debug);
        Block block2 = new Block(testBlock2InputStream, 2, debug);
    }
}
