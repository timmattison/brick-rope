package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.cryptocurrency.bitcoin.HashType;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.interfaces.CheckSigPreprocessor;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

import javax.inject.Inject;
import java.util.logging.Logger;

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
    private final SignatureProcessor signatureProcessor;
    private final ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory;
    private final CheckSigPreprocessor checkSigPreprocessor;
    private Logger logger;

    @Inject
    public OpCheckSig(Logger logger, SignatureProcessor signatureProcessor, ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory, CheckSigPreprocessor checkSigPreprocessor) {
        this.logger = logger;
        this.signatureProcessor = signatureProcessor;
        this.eccMessageSignatureVerifierFactory = eccMessageSignatureVerifierFactory;
        this.checkSigPreprocessor = checkSigPreprocessor;
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

        ECCSignature eccSignature = (ECCSignature) signatureProcessor.getSignature(signature, publicKey);

        // Get the last byte of the signature as the hash type
        HashType hashType = HashType.convert(signature);

        byte[] signedTransactionData = checkSigPreprocessor.preprocessTransactionData(stateMachine.getPreviousTransactionHash(),
                stateMachine.getCurrentTransactionHash(),
                stateMachine.getPreviousOutputIndex(),
                stateMachine.getInputNumber(),
                hashType);

        // Create the signature verifier
        ECCMessageSignatureVerifier eccMessageSignatureVerifier = eccMessageSignatureVerifierFactory.create();

        try {
            // Validate the signature
            //logger.info("txCopyBytes: " + ByteArrayHelper.toHex(txCopyBytes));
            boolean valid = eccMessageSignatureVerifier.signatureValid(signedTransactionData, eccSignature);
            //logger.info((valid ? "Valid" : "Invalid"));

            // Push the result onto the stack
            stateMachine.push(valid ? 1 : 0);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            throw new UnsupportedOperationException(e);
        }
    }
}
