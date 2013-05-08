package com.timmattison.bitcoin.test.script.words.crypto;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.SanityCheckHelper;
import com.timmattison.bitcoin.test.script.StateMachine;
import com.timmattison.bitcoin.test.script.Word;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpCheckSig extends Word {
    private static final String word = "OP_CHECKSIG";
    private static final Byte opcode = (byte) 0xac;
    private byte[] publicKey;
    private byte[] signature;
    private StateMachine stateMachine;
    private byte[] subscript;

    public OpCheckSig() {
        super(word, opcode, false);
    }

    // From the wiki:
    // Firstly always this (the default) procedure is applied:
    // Signature verification process of the default procedure

    // 1. the public key and the signature are popped from the stack, in that order. If the hash-type value is 0, then
    //      it is replaced by the last_byte of the signature. Then the last byte of the signature is always deleted.
    //
    // 2. A new subscript is created from the instruction from the most recently parsed OP_CODESEPARATOR (last one in
    //      script) to the end of the script. If there is no OP_CODESEPARATOR the entire script becomes the subscript
    //      (hereby referred to as subScript)
    //
    // 3. The sig is deleted from subScript.
    //
    // 4. All OP_CODESEPARATORS are removed from subScript
    //
    // 5. The hashtype is removed from the last byte of the sig and stored
    //
    // 6. A copy is made of the current transaction (hereby referred to txCopy)
    //
    // 7. The scripts for all transaction inputs in txCopy are set to empty scripts (exactly 1 byte 0x00)
    //
    // 8. The script for the current transaction input in txCopy is set to subScript (lead in by its length as a var-integer encoded!)

    @Override
    public void execute(StateMachine stateMachine) {
        this.stateMachine = stateMachine;

        step1();

        step2();

        step3();

        throw new UnsupportedOperationException();
    }

    /**
     * The public key and the signature are popped from the stack, in that order.  Hash type is set up.
     */
    private void step1() {
        // Pop the public key from the stack
        publicKey = (byte[]) stateMachine.pop();

        // Sanity check
        SanityCheckHelper.sanityCheckNotNull(publicKey, "Public key");
        getLogger().info("Public key: " + ByteArrayHelper.formatArray(publicKey));

        // Pop the signature from the stack
        signature = (byte[]) stateMachine.pop();

        // Sanity check
        SanityCheckHelper.sanityCheckByteArrayLengthGreaterThan(signature, 1, "Signature");
        getLogger().info("Signature: " + ByteArrayHelper.formatArray(signature));

        // Remove the last byte of the signature
        byte lastByteOfSignature = signature[signature.length - 1];

        // Is the hash type zero?
        if(stateMachine.getHashTypeByte() == 0) {
            // Yes, set the hash type to the last byte of the signature
            stateMachine.setHashTypeByte(lastByteOfSignature);
        }

        // Make sure that getHashType doesn't fail now
        stateMachine.getHashType();

        // Remove the last byte from the signature
        signature = Arrays.copyOfRange(signature, 0, signature.length - 1);
        getLogger().info("Signature: " + ByteArrayHelper.formatArray(signature));
    }

    /**
     * The subscript is created
     */
    private void step2()
    {
        int codeSeparatorPosition = stateMachine.getCodeSeparatorPosition();
        byte[] originalScriptBytes = stateMachine.getScriptBytes();

        // Was there a code separator?
        if(codeSeparatorPosition < 0) {
            // No, just use the whole script

            // Copy it
            subscript = Arrays.copyOf(originalScriptBytes, originalScriptBytes.length);
        }
        else {
            // Yes, just use the script bytes after the separator
            subscript = Arrays.copyOfRange(originalScriptBytes, codeSeparatorPosition, originalScriptBytes.length);
        }
    }

    /**
     * The sig is deleted from the subscript
     */
    private void step3() {
        int index = ByteArrayHelper.indexOf(signature, subscript);

        getLogger().info("Before step 3 signature: " + ByteArrayHelper.formatArray(signature));
        getLogger().info("Before step 3 subscript: " + ByteArrayHelper.formatArray(subscript));

        // TODO - This leaves the PUSH operation on the stack that pushed the signature in the first place
        while(index != -1) {
            // Remove the signature at the index returned
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Get all of the bytes up to the signature
            byteArrayOutputStream.write(subscript, 0, index);

            // Get all of the bytes after the signature
            int endIndex = index + signature.length;
            byteArrayOutputStream.write(subscript, endIndex, subscript.length - endIndex);

            // Dump the byte array
            subscript = byteArrayOutputStream.toByteArray();

            // Find the next index
            index = ByteArrayHelper.indexOf(signature, subscript);
        }

        getLogger().info("After step 3 signature: " + ByteArrayHelper.formatArray(signature));
        getLogger().info("After step 3 subscript: " + ByteArrayHelper.formatArray(subscript));

    }
}
