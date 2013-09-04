package com.timmattison.crypto.ecc.tests;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.ecc.interfaces.ECCPoint;
import com.timmattison.crypto.ecc.interfaces.ECCSignature;
import org.junit.Test;

import java.math.BigInteger;

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
        ECCParameters parameters = ECCTestHelper.getSecp160r1(injector);
        ECCPoint block9PublicKey = ECCTestHelper.getPoint(injector, parameters, getBlock9Transaction1Output1XValue, getBlock9Transaction1Output1YValue);
        ECCSignature signature = ECCTestHelper.getSignature(injector, parameters, block170Transaction1Input1RValue, block170Transaction1Input1SValue, block9PublicKey);
    }
}
