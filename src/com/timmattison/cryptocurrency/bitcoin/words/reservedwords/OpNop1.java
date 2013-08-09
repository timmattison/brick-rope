package com.timmattison.cryptocurrency.bitcoin.words.reservedwords;

import com.timmattison.bitcoin.test.script.Word;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpNop1 extends ReservedWordOp {
    private static final String word = "OP_NOP1";
    private static final Byte opcode = (byte) 0xb0;

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
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
