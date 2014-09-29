package com.timmattison.cryptocurrency.bitcoin.words.arithmetic;

import com.timmattison.cryptocurrency.bitcoin.WordDumper;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpLShift extends ArithmeticOp {
    private static final String word = "OP_LSHIFT";
    private static final Byte opcode = (byte) 0x98;

    @Inject
    public OpLShift(WordDumper wordDumper) {
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
