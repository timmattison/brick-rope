package com.timmattison.cryptocurrency.bitcoin.words.stack;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushData4 extends StackOp {
    private static final String word = "OP_PUSHDATA4";
    private static final Byte opcode = (byte) 0x4e;
    private static final int bytesToPush = 1;
    private byte[] valueToPush;

    @Override
    public byte[] build(byte[] data) {
        valueToPush = Arrays.copyOfRange(data, 0, bytesToPush);
        return Arrays.copyOfRange(data, bytesToPush, data.length);
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
    public void execute(com.timmattison.cryptocurrency.bitcoin.StateMachine stateMachine) {
        stateMachine.push(valueToPush);
    }
}
