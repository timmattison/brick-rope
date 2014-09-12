package com.timmattison.cryptocurrency.bitcoin.words.crypto;

import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifierFactory;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import com.timmattison.cryptocurrency.bitcoin.HashType;
import com.timmattison.cryptocurrency.bitcoin.StateMachine;
import com.timmattison.cryptocurrency.interfaces.CheckSigPreprocessor;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/11/13
 * Time: 11:57 AM
 * To change this template use File | Settings | File Templates.
 */
public class OpCheckMultiSig extends CryptoOp {
    private static final String word = "OP_CHECKMULTISIG";
    private static final Byte opcode = (byte) 0xae;
    private final Logger logger;
    private final SignatureProcessor signatureProcessor;
    private final ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory;
    private final CheckSigPreprocessor checkSigPreprocessor;

    @Inject
    public OpCheckMultiSig(Logger logger, SignatureProcessor signatureProcessor, ECCMessageSignatureVerifierFactory eccMessageSignatureVerifierFactory, CheckSigPreprocessor checkSigPreprocessor) {
        this.logger = logger;
        this.signatureProcessor = signatureProcessor;
        this.eccMessageSignatureVerifierFactory = eccMessageSignatureVerifierFactory;
        this.checkSigPreprocessor = checkSigPreprocessor;
    }

    @Override
    public void execute(StateMachine stateMachine) {
        // Some guidance from https://bitcoin.org/en/developer-reference#term-op-checkmultisig

        // Top value of the stack is the total number of public keys we need to get
        int numberOfPublicKeys = (Integer) stateMachine.pop();

        // Get all of the public keys
        List<byte[]> publicKeys = new ArrayList<byte[]>();

        for (int loop = 0; loop < numberOfPublicKeys; loop++) {
            publicKeys.add((byte[]) stateMachine.pop());
        }

        // Top value of the stack is the total number of signature matches we need
        int numberOfSignatures = (Integer) stateMachine.pop();

        // Sanity check: Number of signatures must be less than or equal to number of keys
        if (numberOfSignatures > numberOfPublicKeys) {
            throw new UnsupportedOperationException("The number of matching signatures required [" + numberOfSignatures + "] is greater than the number of public keys [" + numberOfPublicKeys + "]");
        }

        // Get all of the signatures
        List<byte[]> signatures = new ArrayList<byte[]>();

        // OpCheckSig expects the public key on the top of the stack, then the signature
        for (int loop = 0; loop < numberOfSignatures; loop++) {
            signatures.add((byte[]) stateMachine.pop());
        }

        // Pop one more value off of the stack and throw it away (implementation bug in the original client)
        stateMachine.pop();

        int keysSuccessful = 0;

        // Create the signature verifier
        ECCMessageSignatureVerifier eccMessageSignatureVerifier = eccMessageSignatureVerifierFactory.create();

        Map<HashType, byte[]> hashTypeHashMap = new HashMap<HashType, byte[]>();

        // Loop through all of the signatures
        for (int loop = 0; loop < numberOfSignatures; loop++) {
            byte[] signature = signatures.get(loop);

            // Get the last byte of the signature as the hash type
            HashType hashType = HashType.convert(signature);

            if (hashTypeHashMap.containsKey(hashType)) {
                continue;
            }

            byte[] signedTransactionData = checkSigPreprocessor.preprocessTransactionData(stateMachine.getPreviousTransactionHash(),
                    stateMachine.getCurrentTransactionHash(),
                    stateMachine.getPreviousOutputIndex(),
                    stateMachine.getInputNumber(),
                    hashType);

            hashTypeHashMap.put(hashType, signedTransactionData);
        }

        for (int outerLoop = 0; outerLoop < numberOfSignatures; outerLoop++) {
            for (int innerLoop = 0; innerLoop < numberOfPublicKeys; innerLoop++) {
                byte[] signature = signatures.get(outerLoop);
                byte[] publicKey = publicKeys.get(innerLoop);

                ECCSignature eccSignature = (ECCSignature) signatureProcessor.getSignature(signature, publicKey);

                byte[] signedTransactionData = hashTypeHashMap.get(HashType.convert(signature));

                boolean valid = eccMessageSignatureVerifier.signatureValid(signedTransactionData, eccSignature);

                if (valid) {
                    keysSuccessful++;
                }
            }
        }

        if (keysSuccessful > numberOfSignatures) {
            stateMachine.push(1);
        } else {
            stateMachine.push(0);
        }
    }

    @Override
    public Byte getOpcode() {
        return opcode;
    }

    @Override
    public String getName() {
        return word;
    }
}
