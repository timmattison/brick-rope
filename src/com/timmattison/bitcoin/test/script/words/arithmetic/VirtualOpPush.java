package com.timmattison.bitcoin.test.script.words.arithmetic;

import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualOpPush extends Word {
    private static final String word = "VIRTUAL_OP_PUSH";
    private static final int MIN_OPCODE = 0x01;
    private static final int MAX_OPCODE = 0x4B;

    public VirtualOpPush(int opcode) {
        super(word, opcode);

        // Is the opcode valid?
        if ((opcode < MIN_OPCODE) || (opcode > MAX_OPCODE)) {
            // No, throw an exception
            throw new UnsupportedOperationException("Opcode must be between " + MIN_OPCODE + " and " + MAX_OPCODE + ", saw " + opcode);
        }
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}
