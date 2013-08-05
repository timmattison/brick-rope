package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class VirtualOpPush extends StackOp {
    private static final String word = "VIRTUAL_OP_PUSH";
    private static final int MIN_OPCODE = 0x01;
    private static final int MAX_OPCODE = 0x4B;
    private final Byte opcode;
    private byte[] valueToPush;

    public VirtualOpPush(Byte opcode) {
        this.opcode = opcode;

        // Is the opcode valid?
        if ((opcode < MIN_OPCODE) || (opcode > MAX_OPCODE)) {
            // No, throw an exception
            throw new UnsupportedOperationException("Opcode must be between " + MIN_OPCODE + " and " + MAX_OPCODE + ", saw " + opcode);
        }
    }

    @Override
    public String getName() {
        return word;
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public byte[] build(byte[] data) {
        valueToPush = Arrays.copyOfRange(data, 0, opcode);
        return Arrays.copyOfRange(data, opcode, data.length);
    }

    @Override
    public void execute(StateMachine stateMachine) {
        stateMachine.push(valueToPush);
    }
}
