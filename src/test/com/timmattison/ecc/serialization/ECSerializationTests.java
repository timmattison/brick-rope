package com.timmattison.ecc.serialization;

import com.google.gson.Gson;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.modules.ECCSECTestModule;
import com.timmattison.ecc.ECCTestHelper;
import org.junit.Test;

/**
 * Created by timmattison on 4/30/14.
 */
public class ECSerializationTests {
    Injector injector = Guice.createInjector(new ECCSECTestModule());

    @Test
    public void testECCParametersCanBeSerialized() {
        ECCParameters secp256k1 = ECCTestHelper.getSecp256k1(injector);
        Gson gson = new Gson();
        gson.toJson(secp256k1);
    }
}
