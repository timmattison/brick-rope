package com.timmattison.cryptocurrency.bitcoin.words.stack;

import com.timmattison.cryptocurrency.bitcoin.StateMachine;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushData2 extends StackOp {
    private static final String word = "OP_PUSHDATA2";
    private static final Byte opcode = (byte) 0x4d;
    private byte[] valueToPush;
    private static final int bytesToPush = 2;

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
    public void execute(StateMachine stateMachine) {
        stateMachine.push(valueToPush);
    }
}
