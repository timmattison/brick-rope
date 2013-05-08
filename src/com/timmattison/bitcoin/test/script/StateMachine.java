package com.timmattison.bitcoin.test.script;

import com.timmattison.bitcoin.test.ByteArrayHelper;

import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 9:50 AM
 * To change this template use File | Settings | File Templates.
 */
public class StateMachine {
    private Stack<Object> stack = new Stack<Object>();
    private HashType hashType;
    private byte hashTypeByte = 0;
    private int currentPosition;

    /**
     * The position at which the last code separator was encountered
     */
    private int codeSeparatorPosition = -1;

    /**
     * The bytes for this script
     */
    private byte[] scriptBytes;

    public Object pop() {
        checkScriptBytesNotNull();

        return stack.pop();
    }

    public void push(Object input) {
        checkScriptBytesNotNull();

        stack.push(input);
    }

    public byte[] popByteArray() {

        Object item = stack.pop();

        // TODO - Remove this Byte[] junk
        if (item instanceof Byte[]) {
            return convertByteArray((Byte[]) item);
        } else if (item instanceof byte[]) {
            return (byte[]) item;
        } else {
            throw new UnsupportedOperationException("Item on the top of the stack cannot be converted to a byte array");
        }
    }

    // TODO - Remove this Byte[] junk
    public static byte[] convertByteArray(Byte[] input) {
        Byte[] array = (Byte[]) input;
        byte[] bytes = new byte[array.length];
        for (int loop = 0; loop < array.length; loop++) {
            bytes[loop] = array[loop];
        }

        return bytes;
    }

    public HashType getHashType() {
        checkScriptBytesNotNull();

        // Is the hash type already set?
        if(hashType == null) {
            // No, determine which hash type we want
            if(((hashTypeByte & 31) == HashType.SIGHASH_ALL.getValue())) {
                // TODO - Check the logic on this.  It is missing from the docs.
                hashType = HashType.SIGHASH_ALL;
            }
            else if((hashTypeByte & 31) == HashType.SIGHASH_NONE.getValue()) {
                hashType = HashType.SIGHASH_NONE;
            }
            else if((hashTypeByte & 31) == HashType.SIGHASH_SINGLE.getValue()) {
                hashType = HashType.SIGHASH_SINGLE;
            }
            else if((hashTypeByte & HashType.SIGHASH_ANYONECANPAY.getValue()) == HashType.SIGHASH_ANYONECANPAY.getValue()) {
                hashType = HashType.SIGHASH_SINGLE;
            }
            else {
                throw new UnsupportedOperationException("Unsupported hash type value found: " + hashTypeByte + " [" + ByteArrayHelper.toHex(hashTypeByte) + "]");
            }
        }

        return hashType;
    }

    public void setHashTypeByte(byte hashTypeByte) {
        checkScriptBytesNotNull();
        this.hashTypeByte = hashTypeByte;
        hashType = null;
    }

    public byte getHashTypeByte() {
        checkScriptBytesNotNull();
        return hashTypeByte;
    }

    public void setCodeSeparatorPosition() {
        checkScriptBytesNotNull();
        this.codeSeparatorPosition = currentPosition;
    }

    public void setScriptBytes(byte[] scriptBytes) {
        this.scriptBytes = scriptBytes;
    }

    /**
     * Check to make sure that the script bytes are not-NULL so script execution fails early if someone forgets to set
     * the state machine up properly
     */
    private void checkScriptBytesNotNull() {
        if(scriptBytes == null) {
            throw new UnsupportedOperationException("Script bytes cannot be NULL");
        }
    }

    public Object peek() {
        checkScriptBytesNotNull();
        return stack.peek();
    }
}
