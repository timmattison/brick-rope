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
public class JsonIn extends JsonScriptConverter {
    JsonPrevOut prev_out;
    String coinbase;
    String scriptSig;
    long sequenceNumber = 0xFFFFFFFFL;

    public Byte[] toBytes() throws InstantiationException, IllegalAccessException {
        // Do we have a script?
        if ((coinbase == null) && (scriptSig == null)) {
            // No, throw an exception
            throw new UnsupportedOperationException("No coinbase or scriptSig found in input transaction");
        }

        // Get the data that isn't NULL
        String scriptData = (coinbase != null) ? coinbase : scriptSig;

        List<Byte> returnValue = new ArrayList<Byte>();

        // Add the previous output information
        ByteArrayHelper.addBytes(returnValue, prev_out.toBytes());

        // Convert the script to a real script
        scriptData = JsonScriptConverter.convertOpcodeStringsToBytes(scriptData);

        // Get the script data
        Byte[] scriptBytes = JsonHelper.byteStringToForwardBytes(scriptData);

        // Add the script length
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes(scriptBytes.length));

        // Add the script
        ByteArrayHelper.addBytes(returnValue, scriptBytes);

        // Add the sequence number (this is not included in the JSON so we used a fixed value)
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) sequenceNumber));

        return returnValue.toArray(new Byte[returnValue.size()]);
    }
}
