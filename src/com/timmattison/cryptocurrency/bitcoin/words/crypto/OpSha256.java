package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.bitcoin.test.script.Constants;
import com.timmattison.bitcoin.test.script.SimpleHashingWord;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * <p/>
 * Hashes the value on the top of the stack with SHA-256
 */
public class OpSha256 extends SimpleHashingWord {
    private static final String algorithm = Constants.SHA256_ALGORITHM;
    private static final String word = "OP_SHA256";
    private static final Byte opcode = (byte) 0xa8;

    public OpSha256() {
        super(word, opcode);
    }

    @Override
    public String getHashingAlgorithm() {
        return algorithm;
    }
}
