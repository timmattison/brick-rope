package com.timmattison.cryptocurrency.standard;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;
import com.timmattison.profiling.StandardVariableLengthIntegerUnitTestModule;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

/**
 * Created by timmattison on 5/8/14.
 */
public class StandardVariableLengthIntegerTests {
    private static final long max8BitValue = 0xfd;
    private static final long max16BitValue = 0xffff;
    private static final long max32BitValue = 0xffffffff;

    private VariableLengthIntegerFactory variableLengthIntegerFactory;
    private int testCount = 10000;
    private Random random;

    @Before
    public void setup() {
        Injector injector = Guice.createInjector(new StandardVariableLengthIntegerUnitTestModule());
        variableLengthIntegerFactory = injector.getInstance(VariableLengthIntegerFactory.class);

        // Use a fixed seed so we get consistent test results
        random = new Random(0);
    }

    @Test
    public void testSingleByteValues() throws Throwable {
        for (int loop = 0; loop < testCount; loop++) {
            long value = getSingleByteValue();

            checkValue(value);
        }
    }

    @Test
    public void testTwoByteValues() throws Throwable {
        for (int loop = 0; loop < testCount; loop++) {
            long value = getTwoByteValue();

            checkValue(value);
        }
    }

    @Test
    public void testFourByteValues() throws Throwable {
        for (int loop = 0; loop < testCount; loop++) {
            long value = getFourByteValue();

            checkValue(value);
        }
    }

    @Test
    public void testEightByteValues() throws Throwable {
        for (int loop = 0; loop < testCount; loop++) {
            long value = getEightByteValue();

            checkValue(value);
        }
    }

    private void checkValue(long value) throws Throwable {
        VariableLengthInteger variableLengthInteger1 = variableLengthIntegerFactory.create();
        VariableLengthInteger variableLengthInteger2 = variableLengthIntegerFactory.create();

        try {
            variableLengthInteger1.setValue(value);
            variableLengthInteger2.build(variableLengthInteger1.getValueBytes());

            Assert.assertEquals(variableLengthInteger1.getValue(), variableLengthInteger2.getValue());
        } catch (Throwable t) {
            throw (t);
        }
    }

    private long getSingleByteValue() {
        long value = random.nextLong();

        value = getValueWithinRange(value, 0, max8BitValue);

        return value;
    }

    private long getTwoByteValue() {
        long value = random.nextLong();

        value = getValueWithinRange(value, max8BitValue, max16BitValue);

        return value;
    }

    private long getFourByteValue() {
        long value = random.nextLong();

        value = getValueWithinRange(value, max16BitValue, max32BitValue);

        return value;
    }

    private long getEightByteValue() {
        long value = random.nextLong();

        value = getValueWithinRange(value, max32BitValue, 0xFFFFFFFFFFFFFFFFL);

        return value;
    }

    private long getValueWithinRange(long value, long minValue, long maxValue) {
        value = value % maxValue;
        value = Math.max(minValue, value);
        return value;
    }
}
