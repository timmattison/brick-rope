package com.timmattison.bitcoin.test.script.json;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.EndiannessHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/17/13
 * Time: 8:36 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonPrevOut {
    String hash;
    long n;

    public Byte[] toBytes() {
        List<Byte> returnValue = new ArrayList<Byte>();

        // Add the hash
        ByteArrayHelper.addBytes(returnValue, JsonHelper.byteStringToReversedBytes(hash));

        // Add the "n" (index of the output)
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) n));

        return returnValue.toArray(new Byte[returnValue.size()]);
    }
}
