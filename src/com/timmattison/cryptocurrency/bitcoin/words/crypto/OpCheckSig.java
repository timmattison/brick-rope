package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.cryptocurrency.bitcoin.BitcoinHashType;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;
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
    private final TransactionLocator transactionLocator;

    public OpCheckSig(SignatureProcessorFactory signatureProcessorFactory, ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory, ScriptingFactory scriptingFactory, TransactionLocator transactionLocator) {
        this.signatureProcessorFactory = signatureProcessorFactory;
        this.eccMessageSignatureVerifierFactory = eccMessageSignatureVerifierFactory;
        this.scriptingFactory = scriptingFactory;
        this.transactionLocator = transactionLocator;
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

        //System.out.println("Public key bytes: " + ByteArrayHelper.toHex(publicKey));

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

        //System.out.println("X bytes: " + ByteArrayHelper.toHex(x));
        //System.out.println("Y bytes: " + ByteArrayHelper.toHex(y));

        // Sanity check: x and y are both 32 bytes
        if (x.length != 32) {
            throw new UnsupportedOperationException("x is not 32 bytes");
        }

        if (y.length != 32) {
            throw new UnsupportedOperationException("y is not 32 bytes");
        }

        //System.out.println("Signature bytes: " + ByteArrayHelper.toHex(signature));

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

        //System.out.println("R bytes: " + ByteArrayHelper.toHex(r));
        //System.out.println("S bytes: " + ByteArrayHelper.toHex(s));

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
            throw new UnsupportedOperationException("Only SIGHASH_ALL accepted currently, saw " + hashType);
        }

        // Copy txNew to txCopy, XXX I KNOW THIS ISN'T REALLY A COPY XXX
        Transaction sourceTransaction = transactionLocator.findTransaction(stateMachine.getPreviousTransactionHash());
        Transaction destinationTransaction = transactionLocator.findTransaction(stateMachine.getCurrentTransactionHash());

        // Get the subscript XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        Script subscript = sourceTransaction.getOutputs().get(stateMachine.getPreviousOutputIndex()).getScript();

        // Get the raw bytes so we have a new reference to it and can recreate it later
        byte[] subscriptBytes = subscript.dump();

        // XXX - NEED A REAL COPY HERE OTHERWISE WE DESTROY OUR DATA!
        Transaction txCopy = destinationTransaction;

        // Clear all txIn scripts
        for (Input input : txCopy.getInputs()) {
            input.setScript(null);
        }

        // Copy the subscript into the txIn we're checking XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        InputScript safeSubscript = scriptingFactory.createInputScript(1, subscriptBytes.length, false);
        safeSubscript.build(subscriptBytes);
        txCopy.getInputs().get(stateMachine.getInputNumber()).setScript(safeSubscript);

        //System.out.println("Included subscript: " + txCopy.prettyDump(0));

        // Serialize txCopy
        byte[] txCopyBytes = txCopy.dump();

        // Add on four byte hash type code
        txCopyBytes = ByteArrayHelper.concatenate(txCopyBytes, hashType.getLittleEndianValue());

        // XXX USE THE TRANSACTION LOCATOR!

        // Create the signature processor
        SignatureProcessor signatureProcessor = signatureProcessorFactory.create();
        ECCMessageSignatureVerifier eccMessageSignatureVerifier = eccMessageSignatureVerifierFactory.create();

        // XXX - R and S match what the reference code shows
        ECCSignature eccSignature = (ECCSignature) signatureProcessor.getSignature(r, s, publicKey);

        try {
            // Validate the signature
            boolean valid = eccMessageSignatureVerifier.signatureValid(txCopyBytes, eccSignature);

            // Push the result onto the stack
            stateMachine.push(valid ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UnsupportedOperationException(e);
        }
    }
}
