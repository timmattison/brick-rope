package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

/**
 * Duplicates the top stack item
 */
public class OpDup extends StackOp {
    private static final String word = "OP_DUP";
    private static final Byte opcode = (byte) 0x76;

    @Override
    public void execute(StateMachine stateMachine) {
        Object currentTopOfStack = stateMachine.pop();
        stateMachine.push(currentTopOfStack);
        stateMachine.push(currentTopOfStack);
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }

    @Override
    public byte[] build(byte[] data) {
        return data;
    }
}
