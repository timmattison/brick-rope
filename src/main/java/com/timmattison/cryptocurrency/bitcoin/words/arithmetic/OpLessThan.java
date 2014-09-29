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
public class OpLessThan extends ArithmeticOp {
    private static final String word = "OP_LESSTHAN";
    private static final Byte opcode = (byte) 0x9f;

    @Inject
    public OpLessThan(WordDumper wordDumper) {
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
