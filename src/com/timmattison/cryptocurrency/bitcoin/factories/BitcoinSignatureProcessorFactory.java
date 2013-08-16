package com.timmattison.cryptocurrency.bitcoin.factories;

import com.timmattison.cryptocurrency.bitcoin.BitcoinSignatureProcessor;
import com.timmattison.cryptocurrency.factories.ECCParamsFactory;
import com.timmattison.cryptocurrency.factories.ECCSignatureFactory;
import com.timmattison.cryptocurrency.factories.SignatureProcessorFactory;
import com.timmattison.cryptocurrency.interfaces.SignatureProcessor;

import javax.inject.Inject;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/15/13
 * Time: 6:10 PM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinSignatureProcessorFactory implements SignatureProcessorFactory {
    private final ECCParamsFactory eccParamsFactory;
    private final ECCSignatureFactory signatureFactory;

    @Inject
    public BitcoinSignatureProcessorFactory(ECCParamsFactory eccParamsFactory, ECCSignatureFactory signatureFactory) {
        this.eccParamsFactory = eccParamsFactory;
        this.signatureFactory = signatureFactory;
    }

    @Override
    public SignatureProcessor create() {
        return new BitcoinSignatureProcessor(eccParamsFactory, signatureFactory);
    }
}
