package com.timmattison.bitcoin.test.script.words.constants;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.script.ByteConsumingWord;
import com.timmattison.bitcoin.test.script.StateMachine;

import java.io.ByteArrayInputStream;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:51 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpPushData4 extends ByteConsumingWord {
    private static final String word = "OP_PUSHDATA4";
    private static final Byte opcode = (byte) 0x4e;

    public OpPushData4() {
        super(word, opcode, false);
    }

    @Override
    protected void doAdditionalProcessing(ByteArrayInputStream input) {
        // TODO - This is the same in all three OpPushDataX classes, extract it
        validateFirstStageInput();

        // Our input now represents the number of bytes we are going to read
        int bytesToRead = (int) ((this.input[0] + (this.input[1] << 8) + (this.input[2] << 16) + (this.input[3] << 24)) & 0x7FFFFFFFL);
        int inputSize = input.available();

        // TODO - This is the same in all three OpPushDataX classes, extract it
        validateBytesToRead(bytesToRead, inputSize);

        // TODO - This is the same in all three OpPushDataX classes, extract it
        // Get the bytes we need
        this.input = new byte[bytesToRead];
        input.read(this.input, 0, bytesToRead);
    }

    @Override
    protected boolean isAdditionalProcessingRequired() {
        return true;

    }

    @Override
    public int getInputBytesRequired() {
        return 4;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        throw new UnsupportedOperationException();
    }
}
