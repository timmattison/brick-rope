package com.timmattison.cryptocurrency.bitcoin;

import com.timmattison.cryptocurrency.factories.BlockHeaderFactory;
import com.timmattison.cryptocurrency.factories.TransactionFactory;
import com.timmattison.cryptocurrency.factories.VariableLengthIntegerFactory;
import com.timmattison.cryptocurrency.standard.StandardBlock;

import javax.inject.Inject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 * User: Tim
 * Date: 8/4/13
 * Time: 11:49 AM
 * To change this template use File | Settings | File Templates.
 */
public class BitcoinBlock extends StandardBlock implements Serializable {
    @Inject
    public BitcoinBlock(BlockHeaderFactory blockHeaderFactory, TransactionFactory transactionFactory, VariableLengthIntegerFactory variableLengthIntegerFactory) {
        super(blockHeaderFactory, transactionFactory, variableLengthIntegerFactory);
    }

    @Override
    public byte[] dump() {
        byte[] magic = new byte[]{(byte) 0xf9, (byte) 0xbe, (byte) 0xb4, (byte) 0xd9};

        byte[] bytes = super.dump();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            baos.write(magic);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

        int length = bytes.length;

        baos.write(length & 0xFF);
        baos.write((length >> 8) & 0xFF);
        baos.write((length >> 16) & 0xFF);
        baos.write((length >> 24) & 0xFF);

        try {
            baos.write(bytes);
        } catch (IOException e) {
            throw new UnsupportedOperationException(e);
        }

        return baos.toByteArray();
    }
}
