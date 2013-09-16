package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.cryptocurrency.bitcoin.BitcoinHashType;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.bitcoin.applications.BitcoinValidateBlock170;
import com.timmattison.cryptocurrency.bitcoin.factories.BitcoinScriptingFactory;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.standard.InputScript;
import com.timmattison.cryptocurrency.standard.Script;

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
    private final ScriptingFactory scriptingFactory;
    private final SignatureProcessorFactory signatureProcessorFactory;
    private final ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory;

    public OpCheckSig(SignatureProcessorFactory signatureProcessorFactory, ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory, ScriptingFactory scriptingFactory) {
        this.signatureProcessorFactory = signatureProcessorFactory;
        this.eccMessageSignatureVerifierFactory = eccMessageSignatureVerifierFactory;
        this.scriptingFactory = scriptingFactory;
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
        Transaction transaction0In170 = BitcoinValidateBlock170.transaction0In170;
        Transaction transaction1In170 = BitcoinValidateBlock170.transaction1In170;
        Transaction transaction0In9 = BitcoinValidateBlock170.transaction0In9;

        // Get the subscript XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        Script subscript = transaction0In9.getOutputs().get(0).getScript();

        // Get the raw bytes so we have a new reference to it and can recreate it later
        byte[] subscriptBytes = subscript.dump();

        // XXX - NEED A REAL COPY HERE OTHERWISE WE DESTROY OUR DATA!
        Transaction txCopy = transaction1In170;

        // Clear all txIn scripts
        for (Input input : txCopy.getInputs()) {
            input.setScript(null);
        }

        // Copy the subscript into the txIn we're checking XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        InputScript safeSubscript = scriptingFactory.createInputScript(1, subscriptBytes.length, true);
        safeSubscript.build(subscriptBytes);
        txCopy.getInputs().get(0).setScript(safeSubscript);

        System.out.println("Included subscript: " + txCopy.prettyDump(0));

        // Serialize txCopy
        byte[] txCopyBytes = txCopy.dump();

        byte[] referenceBytes = new byte[] { 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0xc9, (byte) 0x97, (byte) 0xa5, (byte) 0xe5, (byte) 0x6e, (byte) 0x10, (byte) 0x41,
                (byte) 0x02, (byte) 0xfa, (byte) 0x20, (byte) 0x9c, (byte) 0x6a, (byte) 0x85, (byte) 0x2d, (byte) 0xd9, (byte) 0x06, (byte) 0x60, (byte) 0xa2, (byte) 0x0b, (byte) 0x2d, (byte) 0x9c, (byte) 0x35, (byte) 0x24, (byte) 0x23,
                (byte) 0xed, (byte) 0xce, (byte) 0x25, (byte) 0x85, (byte) 0x7f, (byte) 0xcd, (byte) 0x37, (byte) 0x04, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43, (byte) 0x41, (byte) 0x04, (byte) 0x11, (byte) 0xdb,
                (byte) 0x93, (byte) 0xe1, (byte) 0xdc, (byte) 0xdb, (byte) 0x8a, (byte) 0x01, (byte) 0x6b, (byte) 0x49, (byte) 0x84, (byte) 0x0f, (byte) 0x8c, (byte) 0x53, (byte) 0xbc, (byte) 0x1e, (byte) 0xb6, (byte) 0x8a, (byte) 0x38,
                (byte) 0x2e, (byte) 0x97, (byte) 0xb1, (byte) 0x48, (byte) 0x2e, (byte) 0xca, (byte) 0xd7, (byte) 0xb1, (byte) 0x48, (byte) 0xa6, (byte) 0x90, (byte) 0x9a, (byte) 0x5c, (byte) 0xb2, (byte) 0xe0, (byte) 0xea, (byte) 0xdd,
                (byte) 0xfb, (byte) 0x84, (byte) 0xcc, (byte) 0xf9, (byte) 0x74, (byte) 0x44, (byte) 0x64, (byte) 0xf8, (byte) 0x2e, (byte) 0x16, (byte) 0x0b, (byte) 0xfa, (byte) 0x9b, (byte) 0x8b, (byte) 0x64, (byte) 0xf9, (byte) 0xd4,
                (byte) 0xc0, (byte) 0x3f, (byte) 0x99, (byte) 0x9b, (byte) 0x86, (byte) 0x43, (byte) 0xf6, (byte) 0x56, (byte) 0xb4, (byte) 0x12, (byte) 0xa3, (byte) 0xac, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0x02,
                (byte) 0x00, (byte) 0xca, (byte) 0x9a, (byte) 0x3b, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43, (byte) 0x41, (byte) 0x04, (byte) 0xae, (byte) 0x1a, (byte) 0x62, (byte) 0xfe, (byte) 0x09, (byte) 0xc5,
                (byte) 0xf5, (byte) 0x1b, (byte) 0x13, (byte) 0x90, (byte) 0x5f, (byte) 0x07, (byte) 0xf0, (byte) 0x6b, (byte) 0x99, (byte) 0xa2, (byte) 0xf7, (byte) 0x15, (byte) 0x9b, (byte) 0x22, (byte) 0x25, (byte) 0xf3, (byte) 0x74,
                (byte) 0xcd, (byte) 0x37, (byte) 0x8d, (byte) 0x71, (byte) 0x30, (byte) 0x2f, (byte) 0xa2, (byte) 0x84, (byte) 0x14, (byte) 0xe7, (byte) 0xaa, (byte) 0xb3, (byte) 0x73, (byte) 0x97, (byte) 0xf5, (byte) 0x54, (byte) 0xa7,
                (byte) 0xdf, (byte) 0x5f, (byte) 0x14, (byte) 0x2c, (byte) 0x21, (byte) 0xc1, (byte) 0xb7, (byte) 0x30, (byte) 0x3b, (byte) 0x8a, (byte) 0x06, (byte) 0x26, (byte) 0xf1, (byte) 0xba, (byte) 0xde, (byte) 0xd5, (byte) 0xc7,
                (byte) 0x2a, (byte) 0x70, (byte) 0x4f, (byte) 0x7e, (byte) 0x6c, (byte) 0xd8, (byte) 0x4c, (byte) 0xac, (byte) 0x00, (byte) 0x28, (byte) 0x6b, (byte) 0xee, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x43,
                (byte) 0x41, (byte) 0x04, (byte) 0x11, (byte) 0xdb, (byte) 0x93, (byte) 0xe1, (byte) 0xdc, (byte) 0xdb, (byte) 0x8a, (byte) 0x01, (byte) 0x6b, (byte) 0x49, (byte) 0x84, (byte) 0x0f, (byte) 0x8c, (byte) 0x53, (byte) 0xbc,
                (byte) 0x1e, (byte) 0xb6, (byte) 0x8a, (byte) 0x38, (byte) 0x2e, (byte) 0x97, (byte) 0xb1, (byte) 0x48, (byte) 0x2e, (byte) 0xca, (byte) 0xd7, (byte) 0xb1, (byte) 0x48, (byte) 0xa6, (byte) 0x90, (byte) 0x9a, (byte) 0x5c,
                (byte) 0xb2, (byte) 0xe0, (byte) 0xea, (byte) 0xdd, (byte) 0xfb, (byte) 0x84, (byte) 0xcc, (byte) 0xf9, (byte) 0x74, (byte) 0x44, (byte) 0x64, (byte) 0xf8, (byte) 0x2e, (byte) 0x16, (byte) 0x0b, (byte) 0xfa, (byte) 0x9b,
                (byte) 0x8b, (byte) 0x64, (byte) 0xf9, (byte) 0xd4, (byte) 0xc0, (byte) 0x3f, (byte) 0x99, (byte) 0x9b, (byte) 0x86, (byte) 0x43, (byte) 0xf6, (byte) 0x56, (byte) 0xb4, (byte) 0x12, (byte) 0xa3, (byte) 0xac, (byte) 0x00,
                (byte) 0x00, (byte) 0x00, (byte) 0x00, (byte) 0x01, (byte) 0x00, (byte) 0x00, (byte) 0x00};

        // Add on four byte hash type code
        txCopyBytes = ByteArrayHelper.concatenate(txCopyBytes, hashType.getLittleEndianValue());

        System.out.println("reference: " + ByteArrayHelper.toHex(referenceBytes));
        System.out.println("tx copy  : " + ByteArrayHelper.toHex(txCopyBytes));

        if(!ByteArrayHelper.equals(referenceBytes, txCopyBytes)) {
            throw new UnsupportedOperationException("Failed to match reference");
        }

        System.out.println("Included hash type code: " + ByteArrayHelper.toHex(txCopyBytes));

        // Create the signature processor

        //TransactionLocator transactionLocator = injector.getInstance(TransactionLocator.class);
        // XXX USE THE TRANSACTION LOCATOR!

        SignatureProcessor signatureProcessor = signatureProcessorFactory.create();
        ECCMessageSignatureVerifier eccMessageSignatureVerifier = eccMessageSignatureVerifierFactory.create();

        // XXX - R and S match what the reference code shows
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

        // XXX - Does something need to be put on the stack here?
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
