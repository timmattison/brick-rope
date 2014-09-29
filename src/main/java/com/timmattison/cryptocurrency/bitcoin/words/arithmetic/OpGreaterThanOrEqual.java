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
public class OpGreaterThanOrEqual extends ArithmeticOp {
    private static final String word = "OP_GREATERTHANOREQUAL";
    private static final Byte opcode = (byte) 0xa2;

    @Inject
    public OpGreaterThanOrEqual(WordDumper wordDumper) {
        super(wordDumper);
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
