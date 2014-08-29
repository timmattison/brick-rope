package com.timmattison.profiling;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by timmattison on 5/8/14.
 */
public class VariableLengthIntegerProfiling {
    private static final long count = 100000000;
    private Set<VariableLengthIntegerFactory> variableLengthIntegerFactorySet;

    @Before
    public void setup() {
        variableLengthIntegerFactorySet = new HashSet<VariableLengthIntegerFactory>();

        Injector injector = Guice.createInjector(new StandardVariableLengthIntegerUnitTestModule());
        variableLengthIntegerFactorySet.add(injector.getInstance(VariableLengthIntegerFactory.class));

        injector = Guice.createInjector(new NoLoopVariableLengthIntegerUnitTestModule());
        variableLengthIntegerFactorySet.add(injector.getInstance(VariableLengthIntegerFactory.class));
    }

    //@Test
    public void createVariableLengthIntegersWithLong() {
        long expectedValue = 0x0A00AA00L;
        innerCreateVariableLengthIntegers(count, expectedValue, "long");
    }

    //@Test
    public void createVariableLengthIntegersWithInt() {
        long expectedValue = 0x0A00AAL;
        innerCreateVariableLengthIntegers(count, expectedValue, "int");
    }

    //@Test
    public void createVariableLengthIntegersWithShort() {
        long expectedValue = 0x0A00L;
        innerCreateVariableLengthIntegers(count, expectedValue, "short");
    }

    //@Test
    public void createVariableLengthIntegersWithByte() {
        long expectedValue = 0x0A;
        innerCreateVariableLengthIntegers(count, expectedValue, "byte");
    }

    private void innerCreateVariableLengthIntegers(long count, long expectedValue, String valueType) {
        for (VariableLengthIntegerFactory variableLengthIntegerFactory : variableLengthIntegerFactorySet) {
            long start = System.currentTimeMillis();

            VariableLengthInteger variableLengthInteger = variableLengthIntegerFactory.create();
            variableLengthInteger.setValue(expectedValue);

            byte[] bytes = variableLengthInteger.getValueBytes();

            for (int loop = 0; loop < count; loop++) {
                VariableLengthInteger temp = variableLengthIntegerFactory.create();
                temp.build(bytes);

                Assert.assertEquals(expectedValue, temp.getValue());
            }

            long end = System.currentTimeMillis();

            long difference = end - start;

            double millisecondsPerValue = (double) difference / (double) count;

            System.out.println(millisecondsPerValue + " ms per " + valueType + " value from " + variableLengthIntegerFactory.getClass().getSimpleName());
        }
    }
}
