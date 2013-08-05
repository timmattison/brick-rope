package com.timmattison.cryptocurrency.bitcoin.words.arithmetic;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNot extends ArithmeticOp {
    private static final String word = "OP_NOT";
    private static final Byte opcode = (byte) 0x91;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}
