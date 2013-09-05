package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCMessageSignatureVerifier;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/4/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlock170Test {
    Injector injector = Guice.createInjector(new ECCTestModule());

    private static final String block170Transaction1Input1String = "47304402204e45e16932b8af514961a1d3a1a25fdf3f4f7732e9d624c6c61548ab5fb8cd410220181522ec8eca07de4860a4acdd12909d831cc56cbbac4622082221a8768d1d0901";

    /**
     * This unlocks the coins from block 9 so they can be spent
     */
    private static final BigInteger block170Transaction1Input1 = new BigInteger(block170Transaction1Input1String, 16);

    /**
     * The R value from block 170 transaction 1
     */
    private static final int rStart = 5 * 2;
    private static final int rLength = 32;
    private static final int rEnd = rStart + (rLength * 2);
    private static final BigInteger block170Transaction1Input1RValue = new BigInteger(block170Transaction1Input1String.substring(rStart, rEnd), 16);

    /**
     * The S value from block 170 transaction 1
     */
    private static final int sStart = rEnd + (2 * 2);
    private static final int sLength = 32;
    private static final int sEnd = sStart + (sLength * 2);
    private static final BigInteger block170Transaction1Input1SValue = new BigInteger(block170Transaction1Input1String.substring(sStart, sEnd), 16);

    /**
     * These are the script bytes from block 9's output
     */
    private static final String block9Transaction1Output1String = "410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac";
    private static final BigInteger block9Transaction1Output1 = new BigInteger(block9Transaction1Output1String, 16);

    /**
     * The X value from block 9 transaction 1
     */
    private static final int xStart = 2 * 2;
    private static final int xLength = 32;
    private static final int xEnd = xStart + (xLength * 2);
    private static final BigInteger getBlock9Transaction1Output1XValue = new BigInteger(block170Transaction1Input1String.substring(xStart, xEnd), 16);

    /**
     * The Y value from block 9 transaction 1
     */
    private static final int yStart = xEnd;
    private static final int yLength = 32;
    private static final int yEnd = yStart + (yLength * 2);
    private static final BigInteger getBlock9Transaction1Output1YValue = new BigInteger(block170Transaction1Input1String.substring(yStart, yEnd), 16);

    @Test
    public void testBlock170Transaction() {
        BigInteger block9FullTransaction = new BigInteger("01000000c60ddef1b7618ca2348a46e868afc26e3efc68226c78aa47f8488c4000000000c997a5e56e104102fa209c6a852dd90660a20b2d9c352423edce25857fcd37047fca6649ffff001d28404f530101000000010000000000000000000000000000000000000000000000000000000000000000ffffffff0704ffff001d0134ffffffff0100f2052a0100000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac00000000", 16);
        byte[] block9FullTransactionBytes = Arrays.copyOfRange(block9FullTransaction.toByteArray(), 1, block9FullTransaction.toByteArray().length);

        BigInteger block9Output1 = new BigInteger("00f2052a0100000043410411db93e1dcdb8a016b49840f8c53bc1eb68a382e97b1482ecad7b148a6909a5cb2e0eaddfb84ccf9744464f82e160bfa9b8b64f9d4c03f999b8643f656b412a3ac", 16);
        byte[] block9Output1Bytes = Arrays.copyOfRange(block9Output1.toByteArray(), 0, block9Output1.toByteArray().length);

        ECCParameters parameters = ECCTestHelper.getSecp256k1(injector);
        ECCPoint block9PublicKey = ECCTestHelper.getPoint(injector, parameters, getBlock9Transaction1Output1XValue, getBlock9Transaction1Output1YValue);
        ECCSignature signature = ECCTestHelper.getSignature(injector, parameters, block170Transaction1Input1RValue, block170Transaction1Input1SValue, block9PublicKey);
        ECCMessageSignatureVerifier verifier = ECCTestHelper.getSignatureVerifier(injector);
        byte[] messageBytesToCheck;
        boolean valid;

        messageBytesToCheck = Arrays.copyOfRange(block9Transaction1Output1.toByteArray(), 1, block9Transaction1Output1.toByteArray().length);
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = Arrays.copyOfRange(block9Transaction1Output1.toByteArray(), 1, block9Transaction1Output1.toByteArray().length - 1);
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = Arrays.copyOfRange(block9Transaction1Output1.toByteArray(), 2, block9Transaction1Output1.toByteArray().length - 1);
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = Arrays.copyOfRange(block9Transaction1Output1.toByteArray(), 1, block9Transaction1Output1.toByteArray().length - 2);
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = block9FullTransactionBytes;
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = block9Output1Bytes;
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        messageBytesToCheck = Arrays.copyOfRange(block9Output1Bytes, 1, block9Output1Bytes.length);
        valid = verifier.signatureValid(messageBytesToCheck, signature);

        if(!valid) {
            // Oops!
        }
    }
}
