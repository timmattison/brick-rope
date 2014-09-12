package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/14/13
 * Time: 6:08 PM
 * To change this template use File | Settings | File Templates.
 */
public enum HashType {
    /**
     * XXX HACK!
     */
    SIGHASH_ALL_2(0x00000000L),

    /**
     * No special handling.  Sign all of the outputs.
     */
    SIGHASH_ALL(0x00000001L),

    /**
     * Sign none of the outputs.  "I don't care where the Bitcoins go"
     * 1. Output of txCopy is set to a vector of zero size
     * 2. All other inputs aside from the current input in txCopy have their nSequence index set to zero
     */
    SIGHASH_NONE(0x00000002L),

    /**
     * Sign one of the outputs.  "I don't care where the other outputs go"
     * 1. The output of txCopy is resized to the size of the current input index+1.
     * 2. All other txCopy outputs aside from the output that is the same as the current input index are set to a blank script and a value of (long) -1.
     * 3. All other txCopy inputs aside from the current input are set to have an nSequence index of zero.
     */
    SIGHASH_SINGLE(0x00000003L),

    /**
     * "Let other people add inputs to this transaction.  I don't care where the rest of the Bitcoins come from."
     * 1. The txCopy input vector is resized to a length of one.
     * 2. The subScript (lead in by its length as a var-integer encoded!) is set as the first and only member of this vector.
     */
    SIGHASH_ANYONECANPAY(0x00000080L);

    private final long value;
    private Long littleEndianValue;

    private HashType(long value) {
        this.value = value;
    }

    public long getValue() {
        return value;
    }

    public long getLittleEndianValue() {
        if (littleEndianValue == null) {
            littleEndianValue = ((value >> 24) & 0xFF) |
                    (((value >> 16) & 0xFF) << 8) |
                    (((value >> 8) & 0xFF) << 16) |
                    ((value & 0xFF) << 24);
        }

        return littleEndianValue;
    }

    private static HashType convert(long value) {
        // Determine which hash type this is
        if (((value & 31) == HashType.SIGHASH_ALL_2.getValue())) {
            return HashType.SIGHASH_ALL_2;
        } else if (((value & 31) == HashType.SIGHASH_ALL.getValue())) {
            // TODO - Check the logic on this.  It is missing from the docs.
            return HashType.SIGHASH_ALL;
        } else if ((value & 31) == HashType.SIGHASH_NONE.getValue()) {
            return HashType.SIGHASH_NONE;
        } else if ((value & 31) == HashType.SIGHASH_SINGLE.getValue()) {
            return HashType.SIGHASH_SINGLE;
        } else if ((value & HashType.SIGHASH_ANYONECANPAY.getValue()) == HashType.SIGHASH_ANYONECANPAY.getValue()) {
            return HashType.SIGHASH_SINGLE;
        } else {
            return null;
        }
    }

    public static HashType convert(byte[] signature) {
        return HashType.convert(signature[signature.length - 1]);
    }
}
