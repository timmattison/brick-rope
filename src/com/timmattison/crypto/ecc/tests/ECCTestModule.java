package com.timmattison.crypto.ecc.tests;

import com.google.inject.AbstractModule;
import com.google.inject.assistedinject.FactoryModuleBuilder;
import com.timmattison.crypto.ecc.fp.*;
import com.timmattison.crypto.ecc.interfaces.*;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 7/31/13
 * Time: 5:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class ECCTestModule extends AbstractModule {
    @Override
    protected void configure() {
        // XXX - Aren't these redundant?
        bind(ECCCurve.class).to(ECCurveFp.class);
        bind(ECCNamedCurve.class).to(SECNamedCurve.class);
        bind(ECCParameters.class).to(X9ECParameters.class);
        bind(ECCFieldElement.class).to(ECFieldElementFp.class);
        bind(ECCPoint.class).to(ECPointFp.class);

        bind(ECCKeyPair.class).to(ECKeyPairFp.class);
        bind(ECCSignature.class).to(ECSignatureFp.class);

        install(new FactoryModuleBuilder().implement(ECCCurve.class, ECCurveFp.class).build(ECCCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCNamedCurve.class, SECNamedCurve.class).build(ECCNamedCurveFactory.class));
        install(new FactoryModuleBuilder().implement(ECCParameters.class, X9ECParameters.class).build(ECCParametersFactory.class));
        install(new FactoryModuleBuilder().implement(ECCFieldElement.class, ECFieldElementFp.class).build(ECCFieldElementFactory.class));
        install(new FactoryModuleBuilder().implement(ECCPoint.class, ECPointFp.class).build(ECCPointFactory.class));
        install(new FactoryModuleBuilder().implement(ECCKeyPair.class, ECKeyPairFp.class).build(ECCKeyPairFactory.class));
        install(new FactoryModuleBuilder().implement(ECCSignature.class, ECSignatureFp.class).build(ECCSignatureFactory.class));
    }
}
