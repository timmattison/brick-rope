package com.timmattison.ecc.serialization;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.timmattison.crypto.ecc.interfaces.ECCParameters;
import com.timmattison.crypto.modules.ECCSECTestModule;
import com.timmattison.ecc.ECCTestHelper;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by timmattison on 4/30/14.
 */
public class ECSerializationTests {
    Injector injector = Guice.createInjector(new ECCSECTestModule());
    private ECCParameters secp256k1;

    @Before
    public void setup() {
        secp256k1 = ECCTestHelper.getSecp256k1(injector);
    }

    @Test
    public void testECCParametersCanBeSerialized1() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(secp256k1);
        System.out.print(output);
    }

    @Test
    public void testECCCurveCanBeSerialized() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(secp256k1.getCurve());
        System.out.print(output);
    }

    @Test
    public void testECCPointCanBeSerialized() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(secp256k1.getG());
        System.out.print(output);
    }

    @Test
    public void testECCFieldElementCanBeSerialized1() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(secp256k1.getG().getX());
        System.out.print(output);
    }

    @Test
    public void testECCFieldTypeCanBeSerialized() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(secp256k1.getECCFieldType());
        System.out.print(output);
    }
}
