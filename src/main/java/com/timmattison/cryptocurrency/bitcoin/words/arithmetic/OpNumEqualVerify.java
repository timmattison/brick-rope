package com.timmattison.cryptocurrency.bitcoin.words.arithmetic;

import com.google.inject.Inject;
import com.timmattison.cryptocurrency.bitcoin.WordDumper;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNumEqualVerify extends ArithmeticOp {
    private static final String word = "OP_NUMEQUALVERIFY";
    private static final Byte opcode = (byte) 0x9d;

    @Inject
    public OpNumEqualVerify(WordDumper wordDumper) {
        super(wordDumper);
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}
