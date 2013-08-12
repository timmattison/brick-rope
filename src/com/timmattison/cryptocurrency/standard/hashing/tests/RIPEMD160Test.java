package com.timmattison.cryptocurrency.standard.hashing.tests;

import com.timmattison.cryptocurrency.standard.hashing.Endianness;
import com.timmattison.cryptocurrency.standard.hashing.HashFunction;
import com.timmattison.cryptocurrency.standard.hashing.RIPEMD160;
import com.timmattison.cryptocurrency.standard.hashing.chunks.StandardChunkExtractor;
import com.timmattison.cryptocurrency.standard.hashing.padding.StandardMessagePadder;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/16/13
 * Time: 10:26 AM
 * To change this template use File | Settings | File Templates.
 */
public class RIPEMD160Test implements HashTester {
    @Override
    public HashFunction getHashFunction() {
        return new RIPEMD160(new StandardMessagePadder(Endianness.LittleEndian), new StandardChunkExtractor());
    }

    // Test values from RIPEMD160 appendix B
    private String emptyString = "";
    private HashTestVector emptyStringTestVector = new HashTestVector(this, emptyString, "9c1185a5c5e9fc54612808977ee8f548b2258d31");

    private String aString = "a";
    private HashTestVector aStringTestVector = new HashTestVector(this, aString, "0bdc9d2d256b3ee9daae347be6f4dc835a467ffe");

    private String abcString = "abc";
    private HashTestVector abcStringTestVector = new HashTestVector(this, abcString, "8eb208f7e05d987a9b044a8e98c6b087f15a0bfc");

    private String messageDigestString = "message digest";
    private HashTestVector messageDigestStringTestVector = new HashTestVector(this, messageDigestString, "5d0689ef49d2fae572b881b123a85ffa21595f36");

    private String alphabetString1 = "abcdefghijklmnopqrstuvwxyz";
    private HashTestVector alphabetStringTestVector1 = new HashTestVector(this, alphabetString1, "f71c27109c692c1b56bbdceb5b9d2865b3708dbc");

    private String alphabetString2 = "abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq";
    private HashTestVector alphabetStringTestVector2 = new HashTestVector(this, alphabetString2, "12a053384a9c0c88e405a06c27dcf49ada62eb2b");

    private String alphabetString3 = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private HashTestVector alphabetStringTestVector3 = new HashTestVector(this, alphabetString3, "b0e20b6e3116640286ed3a87a5713079b21f5189");

    private String numbersEightTimesString = HashTestUtilities.generateRepetitiveString("1234567890", 8);
    private HashTestVector numbersEightTimesStringTestVector = new HashTestVector(this, numbersEightTimesString, "9b752e45573d4b39f4dbd3323cab82bf63326bfb");

    public String oneMillionAString = HashTestUtilities.generateRepetitiveString("a", 1000000);
    private HashTestVector oneMillionAStringTestVector = new HashTestVector(this, oneMillionAString, "52783243c1697bdbe16d37f97f68f08325dc1528");

    @Test
    public void testEmptyString() throws Exception {
        emptyStringTestVector.runTest();
    }

    @Test
    public void testAString() throws Exception {
        aStringTestVector.runTest();
    }

    @Test
    public void testAbcString() throws Exception {
        abcStringTestVector.runTest();
    }

    @Test
    public void testMessageDigestString() throws Exception {
        messageDigestStringTestVector.runTest();
    }

    @Test
    public void testAlphabetString1() throws Exception {
        alphabetStringTestVector1.runTest();
    }

    @Test
    public void testAlphabetString2() throws Exception {
        alphabetStringTestVector2.runTest();
    }

    @Test
    public void testAlphabetString3() throws Exception {
        alphabetStringTestVector3.runTest();
    }

    @Test
    public void testNumbersEightTimesString() throws Exception {
        numbersEightTimesStringTestVector.runTest();
    }

    @Test
    public void testOneMillionAString() throws Exception {
        oneMillionAStringTestVector.runTest();
    }
}
