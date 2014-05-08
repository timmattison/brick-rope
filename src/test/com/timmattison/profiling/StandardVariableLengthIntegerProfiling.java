package com.timmattison.profiling;

import com.timmattison.cryptocurrency.standard.StandardVariableLengthInteger;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by timmattison on 5/8/14.
 */
public class StandardVariableLengthIntegerProfiling {
    private static final long count = 100000000;

    @Test
    public void createVariableLengthIntegersWithLong() {
        long expectedValue = 0x0A00AA00L;
        innerCreateVariableLengthIntegers(count, expectedValue);
    }

    @Test
    public void createVariableLengthIntegersWithInt() {
        long expectedValue = 0x0A00AAL;
        innerCreateVariableLengthIntegers(count, expectedValue);
    }

    @Test
    public void createVariableLengthIntegersWithShort() {
        long expectedValue = 0x0A00L;
        innerCreateVariableLengthIntegers(count, expectedValue);
    }

    @Test
    public void createVariableLengthIntegersWithByte() {
        long expectedValue = 0x0A;
        innerCreateVariableLengthIntegers(count, expectedValue);
    }

    private void innerCreateVariableLengthIntegers(long count, long expectedValue) {
        long start = System.currentTimeMillis();

        StandardVariableLengthInteger standardVariableLengthInteger = new StandardVariableLengthInteger();
        standardVariableLengthInteger.setValue(expectedValue);

        byte[] bytes = standardVariableLengthInteger.getValueBytes();

        for (int loop = 0; loop < count; loop++) {
            StandardVariableLengthInteger temp = new StandardVariableLengthInteger();
            temp.build(bytes);

            Assert.assertEquals(expectedValue, temp.getValue());
        }

        long end = System.currentTimeMillis();

        long difference = end - start;

        double millisecondsPerValue = (double) difference / (double) count;

        System.out.println(millisecondsPerValue + " ms per value");
    }
}
