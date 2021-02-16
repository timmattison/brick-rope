package com.timmattison.profiling;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.standard.NoLoopVariableLengthInteger;
import com.timmattison.cryptocurrency.standard.interfaces.VariableLengthInteger;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class NoLoopVariableLengthIntegerUnitTestModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new FactoryModuleBuilder().implement(VariableLengthInteger.class, NoLoopVariableLengthInteger.class).build(VariableLengthIntegerFactory.class));
    }
}
