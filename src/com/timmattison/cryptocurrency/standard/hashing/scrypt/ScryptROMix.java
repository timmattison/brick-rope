package com.timmattison.cryptocurrency.standard.hashing.scrypt;

import javax.inject.Inject;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 9/26/13
 * Time: 6:32 PM
 * To change this template use File | Settings | File Templates.
 */
public class ScryptROMix implements ROMix {
    private final BlockMix blockMix;

    @Inject
    public ScryptROMix(BlockMix blockMix) {
        this.blockMix = blockMix;
    }

    @Override
    public byte[] execute(byte[] input, int blockSize, int cpuMemoryCost) {
        // Is there any data?
        if (input == null) {
            // No, throw an exception
            throw new UnsupportedOperationException("ScryptBlockMix data cannot be NULL");
        }

        int expectedNumberOfBytes = (128 * blockSize);

        // Are there there the expected number of bytes?
        if (input.length != expectedNumberOfBytes) {
            // No, throw an exception
            throw new UnsupportedOperationException("ScryptROMix didn't get the correct number of input blocks.  Expected " + expectedNumberOfBytes + ", saw " + input.length);
        }

        int maxCpuMemoryCost = 1 << (128 * blockSize / 8);

        // Is the CPU/memory cost in the correct range?
        if ((cpuMemoryCost < 2) || (cpuMemoryCost >= maxCpuMemoryCost)) {
            // No, throw an exception
            throw new UnsupportedOperationException("The CPU/memory cost in ScryptROMix must be between 1 and " + maxCpuMemoryCost + ", exclusive");
        }

        // Is the CPU/memory cost a power of two?
        // http://stackoverflow.com/questions/600293/how-to-check-if-a-number-is-a-power-of-2
        if (((cpuMemoryCost & (cpuMemoryCost - 1)) != 0)) {
            // No, throw an exception
            throw new UnsupportedOperationException("The CPU/memory cost in ScryptROMix must be a power of two");
        }

        // Create an array for the return value
        byte[] returnValue = Arrays.copyOf(input, input.length);
        byte[] temp = new byte[input.length];

        // First loop
        for (int loop = 0; loop < cpuMemoryCost; loop++) {
        }

        // Second loop
        for (int loop = 0; loop < cpuMemoryCost; loop++) {
        }

        return returnValue;
    }
}
