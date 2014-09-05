package com.timmattison.cryptocurrency.standard.hashing.scrypt;

import com.timmattison.cryptocurrency.helpers.ByteArrayHelper;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScryptBlockMix implements BlockMix {
    private final Salsa salsa;

    @Inject
    public ScryptBlockMix(Salsa salsa) {
        this.salsa = salsa;
    }

    @Override
    public byte[][] execute(byte[][] input, int rounds) {
        // Is there any data?
        if (input == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("ScryptBlockMix data cannot be NULL");
        }

        int expectedNumberOfBlocks = (2 * rounds);

        // Are there there the expected number of blocks?
        if (input.length != expectedNumberOfBlocks) {
            // No, throw an exception
            throw new UnsupportedOperationException("ScryptBlockMix didn't get the correct number of input blocks.  Expected " + expectedNumberOfBlocks + ", saw " + input.length);
        }

        // Create an array for the return value
        byte[][] returnValue = new byte[expectedNumberOfBlocks][];

        // Copy the last block
        byte[] currentBlock = Arrays.copyOf(input[expectedNumberOfBlocks - 1], input[expectedNumberOfBlocks - 1].length);

        for (int loop = 0; loop < (expectedNumberOfBlocks - 1); loop++) {
            byte[] temp = ByteArrayHelper.xor(currentBlock, input[loop]);
            currentBlock = salsa.execute(temp);
            returnValue[loop] = currentBlock;
        }

        return returnValue;
    }
}
