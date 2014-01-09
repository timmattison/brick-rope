package com.timmattison.cryptocurrency.bitcoin.words.bitwiselogic;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpOr extends BitwiseOp {
    private static final String word = "OP_OR";
    private static final Byte opcode = (byte) 0x85;

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}
