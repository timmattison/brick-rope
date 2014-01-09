package com.timmattison.bitcoin.old.script.words.crypto;

import com.timmattison.bitcoin.old.*;
import com.timmattison.bitcoin.old.script.HashType;
import com.timmattison.bitcoin.old.script.ScriptHelper;
import com.timmattison.bitcoin.old.script.StateMachine;
import com.timmattison.bitcoin.old.script.Word;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
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
    private byte[] subscriptBytes;
    private Script subscript;
    private Transaction txCopy;

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

    // Step 5 appears to be a dupe

    @Override
    public void execute(StateMachine stateMachine) {
        this.stateMachine = stateMachine;

        try {
            step1();

            step2();

            step3();

            step4();

            step6();

            step7();

            step8();

            // What kind of hash type is this?
            if(stateMachine.getHashType() == HashType.SIGHASH_ALL) {
                // Standard, just check the signature
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byteArrayOutputStream.write(txCopy.dumpBytes());
                byteArrayOutputStream.write(stateMachine.getHashType().ordinal());
            }
        } catch (Exception e) {
            // Rethrow
            throw new UnsupportedOperationException(e);
        }

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
        if (stateMachine.getHashTypeByte() == 0) {
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
    private void step2() {
        int codeSeparatorPosition = stateMachine.getCodeSeparatorPosition();
        byte[] originalScriptBytes = stateMachine.getScriptBytes();

        // Was there a code separator?
        if (codeSeparatorPosition < 0) {
            // No, just use the whole script

            // Copy it
            subscriptBytes = Arrays.copyOf(originalScriptBytes, originalScriptBytes.length);
        } else {
            // Yes, just use the script bytes after the separator
            subscriptBytes = Arrays.copyOfRange(originalScriptBytes, codeSeparatorPosition, originalScriptBytes.length);
        }
    }

    /**
     * The sig is deleted from the subscript
     * TODO - This can't be done with search and replace
     */
    private void step3() {
        int index = ByteArrayHelper.indexOf(signature, subscriptBytes);

        getLogger().info("Before step 3 signature: " + ByteArrayHelper.formatArray(signature));
        getLogger().info("Before step 3 subscript: " + ByteArrayHelper.formatArray(subscriptBytes));

        while (index != -1) {
            // Move back one byte to get the push opcode
            index--;

            // Remove the signature at the index returned
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            // Get all of the bytes up to the signature
            byteArrayOutputStream.write(subscriptBytes, 0, index);

            // Get all of the bytes after the signature (add 2 to get the hashtype byte too)
            int endIndex = index + signature.length + 2;
            byteArrayOutputStream.write(subscriptBytes, endIndex, subscriptBytes.length - endIndex);

            // Dump the byte array
            subscriptBytes = byteArrayOutputStream.toByteArray();

            // Find the next index
            index = ByteArrayHelper.indexOf(signature, subscriptBytes);
        }

        getLogger().info("After step 3 signature: " + ByteArrayHelper.formatArray(signature));
        getLogger().info("After step 3 subscript: " + ByteArrayHelper.formatArray(subscriptBytes));
    }

    /**
     * Remove all OP_CODESEPARATORS from the subscript
     */
    private void step4() throws IOException {
        subscript = new Script(new ByteArrayInputStream(subscriptBytes), subscriptBytes.length, false);
        subscript.removeCodeSeparators();
    }

    /**
     * Make a copy of the current transaction
     */
    private void step6() throws IOException {
        // Make a copy of the transaction
        txCopy = Transaction.copyTransaction(stateMachine.getCurrentTransaction());
    }

    /**
     * Set all inputs to empty scripts
     */
    private void step7() throws IllegalAccessException, IOException, InstantiationException {
        for(Input input : txCopy.getInputs()) {
            input.setScript(ScriptHelper.getEmptyScriptBytes());
        }
    }

    /**
     * Set the current transaction input in txCopy to our subscript
     */
    private void step8() throws IllegalAccessException, IOException, InstantiationException {
        txCopy.getInput(stateMachine.getReferencedOutputIndex()).setScript(subscriptBytes);
    }
}
