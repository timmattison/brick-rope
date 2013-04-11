package com.timmattison.bitcoin.test.script.ByteConsumingWords.constants;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.ByteConsumingWord;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class Op2 extends ByteConsumingWord {
    private static final String ByteConsumingWord = "OP_2";
    private static final int opcode = 0x52;

    public Op2() {
        super(ByteConsumingWord, opcode);
    }

    @Override
    public int getInputBytesRequired() {
        return 0;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}
