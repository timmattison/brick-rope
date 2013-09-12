package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.cryptocurrency.bitcoin.BitcoinHashType;
import com.timmattison.cryptocurrency.bitcoin.BitcoinInputScript;
import com.timmattison.cryptocurrency.bitcoin.BitcoinModule;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.applications.BitcoinValidateBlock170;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.Script;

import javax.inject.Inject;
import java.util.Arrays;

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
    public SignatureProcessorFactory signatureProcessorFactory;
    @Inject
    public ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory;

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

        System.out.println("Public key bytes: " + ByteArrayHelper.toHex(publicKey));

        // Sanity check: Public key must start with 0x04
        if (publicKey[0] != 0x04) {
            throw new UnsupportedOperationException("Public key does not start with 0x04");
        }

        // Extract X and Y
        int xStart = 1;
        int xEnd = xStart + 32;
        int yStart = xEnd;
        int yEnd = yStart + 32;

        byte[] x = Arrays.copyOfRange(publicKey, xStart, xEnd);
        byte[] y = Arrays.copyOfRange(publicKey, yStart, yEnd);

        System.out.println("X bytes: " + ByteArrayHelper.toHex(x));
        System.out.println("Y bytes: " + ByteArrayHelper.toHex(y));

        // Sanity check: x and y are both 32 bytes
        if (x.length != 32) {
            throw new UnsupportedOperationException("x is not 32 bytes");
        }

        if (y.length != 32) {
            throw new UnsupportedOperationException("y is not 32 bytes");
        }

        System.out.println("Signature bytes: " + ByteArrayHelper.toHex(signature));

        // Sanity check: Signature starts with 0x30
        if (signature[0] != 0x30) {
            throw new UnsupportedOperationException("Signature does not start with 0x30");
        }

        // Sanity check rsLength makes sense
        int rsLength = signature[1];

        if (signature.length - 3 != rsLength) {
            throw new UnsupportedOperationException("rsLength is incorrect");
        }

        // Sanity check: r starts with 0x02
        if (signature[2] != 0x02) {
            throw new UnsupportedOperationException("r does not start with 0x02");
        }

        // TODO Sanity check: rLength makes sense
        int rLength = signature[3];

        // Extract r
        int rStart = 3 + 1;
        int rEnd = rStart + rLength;
        byte[] r = Arrays.copyOfRange(signature, rStart, rEnd);

        // TODO Sanity check: sLength makes sense
        int sLength = signature[rEnd + 1];

        // Extract s
        int sStart = rEnd + 2;
        int sEnd = sStart + sLength;
        byte[] s = Arrays.copyOfRange(signature, sStart, sEnd);

        System.out.println("R bytes: " + ByteArrayHelper.toHex(r));
        System.out.println("S bytes: " + ByteArrayHelper.toHex(s));

        // Get the last byte of the signature as the hash type
        BitcoinHashType hashType = BitcoinHashType.convert(signature[signature.length - 1]);

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

        if (hashType != BitcoinHashType.SIGHASH_ALL) {
            throw new UnsupportedOperationException("Only SIGHASH_ALL accepted currently");
        }

        // Copy txNew to txCopy, XXX I KNOW THIS ISN'T REALLY A COPY XXX
        Transaction transaction1In170 = BitcoinValidateBlock170.transaction1In170;
        Script subscript = transaction1In170.getInputs().get(0).getScript();
        byte[] subscriptBytes = subscript.dump();

        // XXX - NEED A REAL COPY HERE OTHERWISE WE DESTROY OUR DATA!
        Transaction txCopy = transaction1In170;

        System.out.println("Transaction in 170: " + ByteArrayHelper.toHex(txCopy.dump()));

        // Clear all txIn scripts
        for (Input input : txCopy.getInputs()) {
            input.setScript(null);
        }

        System.out.println("Cleared out inputs: " + ByteArrayHelper.toHex(txCopy.dump()));
        System.out.println("Cleared out inputs: " + txCopy.prettyDump(0));

        // Get the subscript XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        Transaction transaction0In9 = BitcoinValidateBlock170.transaction0In9;
        subscript = transaction1In170.getInputs().get(0).getScript();

        System.out.println("Subscript: " + ByteArrayHelper.toHex(subscriptBytes));

        // Copy the subscript into the txIn we're checking XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        InputScript safeSubscript = inputScriptFactory.create(subscriptBytes);
        txCopy.getInputs().get(0).setScript(subscriptBytes);

        System.out.println("Included subscript: " + ByteArrayHelper.toHex(txCopy.dump()));
        System.out.println("Included subscript: " + txCopy.prettyDump(0));

        // Serialize txCopy
        byte[] txCopyBytes = txCopy.dump();

        // Add on four byte hash type code
        txCopyBytes = ByteArrayHelper.concatenate(txCopyBytes, hashType.getLittleEndianValue());

        System.out.println("Included hash type code: " + ByteArrayHelper.toHex(txCopyBytes));

        // Create the signature processor
        // XXX UBER TEMP INSANITY XXX
        Injector injector = Guice.createInjector(new BitcoinModule());

        //TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        // XXX USE THE TRANSACTION LOCATOR!

        signatureProcessorFactory = injector.getInstance(SignatureProcessorFactory.class);
        SignatureProcessor signatureProcessor = signatureProcessorFactory.create();

        eccMessageSignatureVerifierFactory = injector.getInstance(ECCMessageSignatureVerifierFactory.class);
        ECCMessageSignatureVerifier eccMessageSignatureVerifier = eccMessageSignatureVerifierFactory.create();

        ECCSignature eccSignature = (ECCSignature) signatureProcessor.getSignature(r, s, publicKey);

        try {
            boolean valid = false;

            //message[0] += 1;
            valid = eccMessageSignatureVerifier.signatureValid(txCopyBytes, eccSignature);

            if (!valid) {
                throw new UnsupportedOperationException("Signature isn't valid!");
            }
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        throw new UnsupportedOperationException("Not finished yet");
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
