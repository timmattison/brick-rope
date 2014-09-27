package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.bitcoin.words.crypto.OpCodeSeparator;
import com.timmattison.cryptocurrency.factories.ScriptingFactory;
import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;
import com.timmattison.cryptocurrency.interfaces.CheckSigPreprocessor;
import com.timmattison.cryptocurrency.interfaces.Input;
import com.timmattison.cryptocurrency.interfaces.Transaction;
import com.timmattison.cryptocurrency.interfaces.TransactionLocator;
import com.timmattison.cryptocurrency.standard.interfaces.InputScript;
import com.timmattison.cryptocurrency.standard.interfaces.Script;
import org.apache.commons.lang.ArrayUtils;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created by timmattison on 9/10/14.
 */
public class BitcoinCheckSigPreprocessor implements CheckSigPreprocessor {
    private final TransactionLocator transactionLocator;
    private final ScriptingFactory scriptingFactory;

    @Inject
    public BitcoinCheckSigPreprocessor(TransactionLocator transactionLocator, ScriptingFactory scriptingFactory) {
        this.transactionLocator = transactionLocator;
        this.scriptingFactory = scriptingFactory;
    }

    @Override
    public byte[] preprocessTransactionData(byte[] previousTransactionHash, byte[] currentTransactionHash, int previousOutputIndex, int inputNumber, HashType hashType) {
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

        if ((hashType != HashType.SIGHASH_ALL) && (hashType != HashType.SIGHASH_ALL_2)) {
            throw new UnsupportedOperationException("Only SIGHASH_ALL accepted currently, saw " + hashType);
        }

        // Copy txNew to txCopy
        Transaction sourceTransaction = transactionLocator.findTransaction(previousTransactionHash);
        Transaction destinationTransaction = transactionLocator.findTransaction(currentTransactionHash);

        // Get the subscript XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        Script subscript = sourceTransaction.getOutputs().get(previousOutputIndex).getScript();

        // Get the raw bytes so we have a new reference to it and can recreate it later
        byte[] subscriptBytes = subscript.dump();

        // XXX - NEED A REAL COPY HERE OTHERWISE WE DESTROY OUR DATA!
        Transaction txCopy = destinationTransaction;

        //logger.info("Previous TX hash: " + ByteArrayHelper.toHex(previousTransactionHash));
        //logger.info("Before: " + ByteArrayHelper.toHex(txCopy.dump()));

        // Clear all txIn scripts
        for (Input input : txCopy.getInputs()) {
            input.setScript(null);
        }

        //logger.info("After: " + ByteArrayHelper.toHex(txCopy.dump()));

        byte[] finalSubscriptBytes = new byte[0];

        while (subscriptBytes.length > 0) {
            // Get the word that the next byte corresponds to
            Word currentWord = scriptingFactory.createWord(subscriptBytes[0]);

            //if(!(currentWord instanceof OpCodeSeparator)) {
            //    // Add the first byte onto our final bytes
            //    ArrayUtils.addAll(finalSubscriptBytes, Arrays.copyOfRange(subscriptBytes, 0, 1));
            //}

            // Build the current word and get the remaining script data
            subscriptBytes = currentWord.build(Arrays.copyOfRange(subscriptBytes, 1, subscriptBytes.length));

            if (!(currentWord instanceof OpCodeSeparator)) {
                // Add the rest of the data onto our final bytes
                finalSubscriptBytes = ArrayUtils.addAll(finalSubscriptBytes, currentWord.dump());
            }
        }

        // Copy the subscript into the txIn we're checking XXX NEED TO CHECK FOR OP_CODESEPARATORS! XXX
        InputScript safeSubscript = scriptingFactory.createInputScript(1, finalSubscriptBytes.length, false);
        safeSubscript.build(finalSubscriptBytes);
        txCopy.getInputs().get(inputNumber).setScript(safeSubscript);

        //System.out.println("Included subscript: " + txCopy.prettyDump(0));

        // Serialize txCopy
        byte[] txCopyBytes = txCopy.dump();

        // Add on four byte hash type code
        txCopyBytes = ByteArrayHelper.concatenate(txCopyBytes, hashType.getLittleEndianValue());
        return txCopyBytes;
    }
}
