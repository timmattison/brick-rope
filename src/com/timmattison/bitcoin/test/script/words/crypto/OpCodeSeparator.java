package com.timmattison.bitcoin.test.script.words.crypto;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpCodeSeparator extends Word {
    private static final String word = "OP_CODESEPARATOR";
    private static final Byte opcode = (byte) 0xab;
    private int position;

    public OpCodeSeparator() {
        super(word, opcode, false);
    }

    public void setPosition(int value) {
        this.position = value;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        stateMachine.setCodeSeparatorPosition();
    }
}
