package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.BigIntegerHelper;
import com.timmattison.cryptocurrency.bitcoin.BitcoinHashType;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.applications.BitcoinValidateBlock170;
import com.timmattison.crypto.ecc.fp.ECPointFp;
import com.timmattison.crypto.ecc.fp.ECSignatureFp;
import com.timmattison.cryptocurrency.factories.ECCKeyPairFactory;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

import javax.inject.Inject;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpCheckSig extends CryptoOp {
    private static final String word = "OP_CHECKSIG";
    private static final Byte opcode = (byte) 0xac;

    @Inject
    public ECCKeyPairFactory keyFactory;
    @Inject
    public SignatureProcessorFactory signatureProcessorFactory;

    public OpCheckSig() {
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
        // Pop the public key and the signature from the stack
        byte[] publicKey = (byte[]) stateMachine.pop();
        byte[] signature = (byte[]) stateMachine.pop();

        /*
            Signature should be in this format (from http://www.bitcoinsecurity.org/2012/07/22/7/):
             [sig] = [sigLength][0×30][rsLength][0×02][rLength][sig_r][0×02][sLength][sig_s][0×01]
               where
                 sigLength gives the number of bytes taken up the rest of the signature ([0×30]…[0×01])
                 rsLength gives the number of bytes in [0×02][rLength][sig_r][0×02][sLength][sig_s]
                 rLength gives the number of bytes in [sig_r] (approx 32 bytes)
                 sLength gives the number of bytes in [sig_s] (approx 32 bytes)

             [pubKeyHash] = [pubKeyHashLength][RIPEMD160(SHA256(public key))]
               where
                 pubKeyHashLength is always 0×14 (= 20) since the RIPEMD160 digest is 20 bytes.

             [pubKey] (uncompressed) = [publicKeyLength][0×04][keyX][keyY]
               where
                 publicKeyLength is always 0×41 (= 65) since keyX and keyY are 32 bytes and 0×04 is 1 byte

             [pubKey] (compressed) = [publicKeyLength][0×02 or 0×03][keyX]
               where
                 publicKeyLength is always 0×21 (= 33) since keyX is 32 bytes and 0×02/0×03 is 1 byte.
        */

        // Get the last byte of the signature as the hash type
        BitcoinHashType hashType = BitcoinHashType.convert(signature[signature.length - 1]);

        if (hashType != BitcoinHashType.SIGHASH_ALL) {
            throw new UnsupportedOperationException("Only SIGHASH_ALL accepted currently");
        }

        // Create the signature processor
        // XXX UBER TEMP INSANITY XXX
        Injector injector = Guice.createInjector(new BitcoinModule());

        SignatureProcessor signatureProcessor = injector.getInstance(SignatureProcessorFactory.class).create();

        ECSignatureFp ecSignature = (ECSignatureFp) signatureProcessor.getSignature(signature, publicKey);

        try {
            byte[] message = BitcoinValidateBlock170.validationScript.dump();
            message[0] += 1;
            boolean valid = validateSignature(BitcoinValidateBlock170.validationScript.dump(), ecSignature);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        throw new UnsupportedOperationException("Not finished yet");
    }

    private boolean validateSignature(byte[] messageBytes, ECSignatureFp signature) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA1");
        md.update(messageBytes);
        byte[] hashBytes = md.digest();
        String H = ByteArrayHelper.toHex(hashBytes);

        // Calculate e
        BigInteger e = ECHelperFp.calculateE(signature.getEccParameters(), H, hashBytes);

        // Compute u1
        BigInteger u1 = e.multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute u2
        BigInteger u2 = signature.getR().multiply(signature.getS().modPow(BigInteger.ONE.negate(), signature.getN())).mod(signature.getN());

        // Compute R = (xR, yR) = u1G + u2Qu
        ECPointFp u1G = signature.getG().multiply(u1);
        ECPointFp u2Qu = signature.getQu().multiply(u2);

        ECPointFp R = u1G.add(u2Qu);

        // v = xR mod n
        BigInteger v = R.getX().toBigInteger().mod(signature.getN());

        // Validate that v == r, are they equal?
        if(!BigIntegerHelper.equals(v, R.getX().toBigInteger())) {
            // No, return failure
            return false;
        }

        // The message is valid, return success
        return true;
    }
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

    /*
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

                byte[] hashedTransaction = HashHelper.doubleSha256Hash(byteArrayOutputStream.toByteArray());
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
    /*
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
    /*
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
    /*
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
    /*
    private void step4() throws IOException {
        subscript = new Script(new ByteArrayInputStream(subscriptBytes), subscriptBytes.length, false);
        subscript.removeCodeSeparators();
    }

    /**
     * Make a copy of the current transaction
     */
   /*
    private void step6() throws IOException {
        // Make a copy of the transaction
        txCopy = Transaction.copyTransaction(stateMachine.getCurrentTransaction());
    }

    /**
     * Set all inputs to empty scripts
     */
   /*
    private void step7() throws IllegalAccessException, IOException, InstantiationException {
        for(Input input : txCopy.getInputs()) {
            input.setScript(ScriptHelper.getEmptyScriptBytes());
        }
    }

    /**
     * Set the current transaction input in txCopy to our subscript
     */
   /*
    private void step8() throws IllegalAccessException, IOException, InstantiationException {
        txCopy.getInput(stateMachine.getReferencedOutputIndex()).setScript(subscriptBytes);
    }
    */
