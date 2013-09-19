package com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.Word;
import com.timmattison.cryptocurrency.bitcoin.words.flowcontrol.OpVerify;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpEqualVerify extends BitwiseOp {
    private static final String word = "OP_EQUALVERIFY";
    private static final Byte opcode = (byte) 0x88;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        Word equal = new OpEqual();
        Word verify = new OpVerify();

        equal.execute(stateMachine);
        verify.execute(stateMachine);
    }
}
