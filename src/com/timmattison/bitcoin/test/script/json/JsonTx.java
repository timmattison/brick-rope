package com.timmattison.bitcoin.test.script.json;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.EndiannessHelper;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/17/13
 * Time: 8:34 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonTx {
    String hash;
    long ver;
    long vin_sz;
    long vout_sz;
    long lock_time;
    long size;
    List<JsonIn> in;
    List<JsonOut> out;

    public Byte[] toBytes() throws IllegalAccessException, InstantiationException, ParseException {
        List<Byte> returnValue = new ArrayList<Byte>();

        // Add the version
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) ver));

        // Add the input counter
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes((int) vin_sz));

        // Add the inputs
        for (JsonIn jsonIn : in) {
            ByteArrayHelper.addBytes(returnValue, jsonIn.toBytes());
        }

        // Add the output counter
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes((int) vout_sz));

        // Add the outputs
        for (JsonOut jsonOut : out) {
            ByteArrayHelper.addBytes(returnValue, jsonOut.toBytes());
        }

        // Add the lock time
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes((int) lock_time));

        return returnValue.toArray(new Byte[returnValue.size()]);
    }
}
