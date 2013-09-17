package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCKeyPair;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/23/13
 * Time: 7:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class ECMessageSignatureVerifierFpTests {
    static Injector injector = Guice.createInjector(new ECCTestModule());

    private static final BigInteger gec2_2_1_3_r = new BigInteger("1176954224688105769566774212902092897866168635793");
    private static final BigInteger gec2_2_1_3_s = new BigInteger("299742580584132926933316745664091704165278518100");
    private static final BigInteger gec2_2_1_3_dU = new BigInteger("971761939728640320549601132085879836204587084162");
    private static final byte[] gec2_2_1_3_messageBytes = "abc".getBytes();
    private static final ECCKeyPair gec2_eccKeyPair = ECCTestHelper.getKeyPair(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_dU);
    private static final ECCPoint gec2_2_1_4_Qu = gec2_eccKeyPair.getQ();
    private static final ECCSignature gec2_2_1_4_validSignature = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_r, gec2_2_1_3_s, gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureRIsZero = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), BigInteger.ZERO, gec2_2_1_3_s, gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureSIsZero = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_r, BigInteger.ZERO, gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureRIsEqualToN = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), ECCTestHelper.getSecp160r1(injector).getN(), gec2_2_1_3_s, gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureSIsEqualToN = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_r, ECCTestHelper.getSecp160r1(injector).getN(), gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureRIsGreaterThanN = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), ECCTestHelper.getSecp160r1(injector).getN().add(BigInteger.ONE), gec2_2_1_3_s, gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_signatureSIsGreaterThanN = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_r, ECCTestHelper.getSecp160r1(injector).getN().add(BigInteger.ONE), gec2_2_1_4_Qu);
    private static final ECCSignature gec2_2_1_4_invalidSignature = ECCTestHelper.getSignature(injector, ECCTestHelper.getSecp160r1(injector), gec2_2_1_3_r, gec2_2_1_3_s, gec2_2_1_4_Qu.twice());

    //private static final BigInteger block170Transaction1Input0RValue = new BigInteger("35403870366028504256996810077444802381610204463951253825639898172590680231233");
    //private static final BigInteger block170Transaction1Input0SValue = new BigInteger("10892853191319399759538221348851038436710718244456294856259711618427711724809");
    private static final byte[] block170Transaction1Input0RValue = new byte[]{78, 69, -31, 105, 50, -72, -81, 81, 73, 97, -95, -45, -95, -94, 95, -33, 63, 79, 119, 50, -23, -42, 36, -58, -58, 21, 72, -85, 95, -72, -51, 65};
    private static final byte[] block170Transaction1Input0SValue = new byte[]{24, 21, 34, -20, -114, -54, 7, -34, 72, 96, -92, -84, -35, 18, -112, -99, -125, 28, -59, 108, -69, -84, 70, 34, 8, 34, 33, -88, 118, -115, 29, 9};
    private static final byte[] block170Transaction1Input0PublicKey = new byte[]{4, 17, -37, -109, -31, -36, -37, -118, 1, 107, 73, -124, 15, -116, 83, -68, 30, -74, -118, 56, 46, -105, -79, 72, 46, -54, -41, -79, 72, -90, -112, -102, 92, -78, -32, -22, -35, -5, -124, -52, -7, 116, 68, 100, -8, 46, 22, 11, -6, -101, -117, 100, -7, -44, -64, 63, -103, -101, -122, 67, -10, 86, -76, 18, -93};
    private static final ECCSignature block170Transaction1InputSignature = (ECCSignature) ECCTestHelper.getSignature(injector, block170Transaction1Input0RValue, block170Transaction1Input0SValue, block170Transaction1Input0PublicKey);
    private static final byte[] processedScriptForBlock170Transaction1Input = new byte[]{0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xc9, (byte) 0x97, (byte) 0xa5, (byte) 0xe5, (byte) 0x6e, (byte) 0x10, (byte) 0x41,
            (byte) 0x02, (byte) 0xfa, (byte) 0x20, (byte) 0x9c, (byte) 0x6a, (byte) 0x85, (byte) 0x2d, (byte) 0xd9, (byte) 0x06, (byte) 0x60, (byte) 0xa2, (byte) 0x0b, (byte) 0x2d, (byte) 0x9c, (byte) 0x35, (byte) 0x24, (byte) 0x23,
            (byte) 0xed, (byte) 0xce, (byte) 0x25, (byte) 0x85, (byte) 0x7f, (byte) 0xcd, (byte) 0x37, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43, (byte) 0x41, (byte) 0x04, (byte) 0x11, (byte) 0xdb,
            (byte) 0x93, (byte) 0xe1, (byte) 0xdc, (byte) 0xdb, (byte) 0x8a, (byte) 0x01, (byte) 0x6b, (byte) 0x49, (byte) 0x84, (byte) 0x0f, (byte) 0x8c, (byte) 0x53, (byte) 0xbc, (byte) 0x1e, (byte) 0xb6, (byte) 0x8a, (byte) 0x38,
            (byte) 0x2e, (byte) 0x97, (byte) 0xb1, (byte) 0x48, (byte) 0x2e, (byte) 0xca, (byte) 0xd7, (byte) 0xb1, (byte) 0x48, (byte) 0xa6, (byte) 0x90, (byte) 0x9a, (byte) 0x5c, (byte) 0xb2, (byte) 0xe0, (byte) 0xea, (byte) 0xdd,
            (byte) 0xfb, (byte) 0x84, (byte) 0xcc, (byte) 0xf9, (byte) 0x74, (byte) 0x44, (byte) 0x64, (byte) 0xf8, (byte) 0x2e, (byte) 0x16, (byte) 0x0b, (byte) 0xfa, (byte) 0x9b, (byte) 0x8b, (byte) 0x64, (byte) 0xf9, (byte) 0xd4,
            (byte) 0xc0, (byte) 0x3f, (byte) 0x99, (byte) 0x9b, (byte) 0x86, (byte) 0x43, (byte) 0xf6, (byte) 0x56, (byte) 0xb4, (byte) 0x12, (byte) 0xa3, (byte) 0xac, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x02,
            (byte) 0x00, (byte) 0xca, (byte) 0x9a, (byte) 0x3b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43, (byte) 0x41, (byte) 0x04, (byte) 0xae, (byte) 0x1a, (byte) 0x62, (byte) 0xfe, (byte) 0x09, (byte) 0xc5,
            (byte) 0xf5, (byte) 0x1b, (byte) 0x13, (byte) 0x90, (byte) 0x5f, (byte) 0x07, (byte) 0xf0, (byte) 0x6b, (byte) 0x99, (byte) 0xa2, (byte) 0xf7, (byte) 0x15, (byte) 0x9b, (byte) 0x22, (byte) 0x25, (byte) 0xf3, (byte) 0x74,
            (byte) 0xcd, (byte) 0x37, (byte) 0x8d, (byte) 0x71, (byte) 0x30, (byte) 0x2f, (byte) 0xa2, (byte) 0x84, (byte) 0x14, (byte) 0xe7, (byte) 0xaa, (byte) 0xb3, (byte) 0x73, (byte) 0x97, (byte) 0xf5, (byte) 0x54, (byte) 0xa7,
            (byte) 0xdf, (byte) 0x5f, (byte) 0x14, (byte) 0x2c, (byte) 0x21, (byte) 0xc1, (byte) 0xb7, (byte) 0x30, (byte) 0x3b, (byte) 0x8a, (byte) 0x06, (byte) 0x26, (byte) 0xf1, (byte) 0xba, (byte) 0xde, (byte) 0xd5, (byte) 0xc7,
            (byte) 0x2a, (byte) 0x70, (byte) 0x4f, (byte) 0x7e, (byte) 0x6c, (byte) 0xd8, (byte) 0x4c, (byte) 0xac, (byte) 0x00, (byte) 0x28, (byte) 0x6b, (byte) 0xee, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43,
            (byte) 0x41, (byte) 0x04, (byte) 0x11, (byte) 0xdb, (byte) 0x93, (byte) 0xe1, (byte) 0xdc, (byte) 0xdb, (byte) 0x8a, (byte) 0x01, (byte) 0x6b, (byte) 0x49, (byte) 0x84, (byte) 0x0f, (byte) 0x8c, (byte) 0x53, (byte) 0xbc,
            (byte) 0x1e, (byte) 0xb6, (byte) 0x8a, (byte) 0x38, (byte) 0x2e, (byte) 0x97, (byte) 0xb1, (byte) 0x48, (byte) 0x2e, (byte) 0xca, (byte) 0xd7, (byte) 0xb1, (byte) 0x48, (byte) 0xa6, (byte) 0x90, (byte) 0x9a, (byte) 0x5c,
            (byte) 0xb2, (byte) 0xe0, (byte) 0xea, (byte) 0xdd, (byte) 0xfb, (byte) 0x84, (byte) 0xcc, (byte) 0xf9, (byte) 0x74, (byte) 0x44, (byte) 0x64, (byte) 0xf8, (byte) 0x2e, (byte) 0x16, (byte) 0x0b, (byte) 0xfa, (byte) 0x9b,
            (byte) 0x8b, (byte) 0x64, (byte) 0xf9, (byte) 0xd4, (byte) 0xc0, (byte) 0x3f, (byte) 0x99, (byte) 0x9b, (byte) 0x86, (byte) 0x43, (byte) 0xf6, (byte) 0x56, (byte) 0xb4, (byte) 0x12, (byte) 0xa3, (byte) 0xac, (byte) 0x00,
            (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00};


    @Test
    public void block170Transaction1Input0Test() {
        Assert.assertTrue(ECCTestHelper.getSignatureVerifier(injector).signatureValid(processedScriptForBlock170Transaction1Input, block170Transaction1InputSignature));
    }

    /**
     * GEC2 2.1.4 - Verifying operation for V
     */
    @Test
    public void testGec2_2_1_4() {
        Assert.assertTrue(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_validSignature));
    }

    @Test
    public void testRIsZeroReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureRIsZero));
    }

    @Test
    public void testSIsZeroReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureSIsZero));
    }

    @Test
    public void testRIsEqualToNReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureRIsEqualToN));
    }

    @Test
    public void testSIsEqualToNReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureSIsEqualToN));
    }

    @Test
    public void testRIsGreaterThanNReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureRIsGreaterThanN));
    }

    @Test
    public void testSIsGreaterThanNReturnsFalse() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_signatureSIsGreaterThanN));
    }

    @Test
    public void testFailsWithInvalidSignature() {
        Assert.assertFalse(ECCTestHelper.getSignatureVerifier(injector).signatureValid(gec2_2_1_3_messageBytes, gec2_2_1_4_invalidSignature));
    }
}
