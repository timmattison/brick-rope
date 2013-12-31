package com.timmattison.cryptocurrency.bitcoin.words.flowcontrol;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpVerify extends FlowControlOp {
    private static final String word = "OP_VERIFY";
    private static final Byte opcode = (byte) 0x69;

    @Override
    public void execute(StateMachine stateMachine) {
        // Is the top stack value a 1?
        Object value = stateMachine.pop();

        if(!(value instanceof Integer)) {
            throw new UnsupportedOperationException("Top stack value is not an integer");
        }

        int intValue = (Integer) value;

        if(intValue == 0) {
            // Put the zero value back
            stateMachine.push(0);
            throw new UnsupportedOperationException("Top stack value is zero");
        }
        else if(intValue != 1) {
            throw new UnsupportedOperationException("Top stack value is not one");
        }
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
