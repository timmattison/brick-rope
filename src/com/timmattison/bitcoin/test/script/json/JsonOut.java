package com.timmattison.bitcoin.test.script.json;

import com.timmattison.bitcoin.test.ByteArrayHelper;
import com.timmattison.bitcoin.test.EndiannessHelper;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/17/13
 * Time: 8:37 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonOut {
    String value;
    String scriptPubKey;

    private static final int SatoshiPerBitcoin = 100000000;

    public Byte[] toBytes() throws ParseException, InstantiationException, IllegalAccessException {
        List<Byte> returnValue = new ArrayList<Byte>();

        // Parse the value into a BigDecimal
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setParseBigDecimal(true);
        BigDecimal bigDecimalValue = (BigDecimal) decimalFormat.parse(value);

        // The BigDecimal value is in Satoshi (1/100,000,000th of a Bitcoin) so
        //   we need to multiply the value by 100,000,000 to get the value that
        //   goes in the data stream
        bigDecimalValue = bigDecimalValue.multiply(new BigDecimal(SatoshiPerBitcoin));

        // Convert the BigDecimal value into an 8-byte unsigned value (we really don't care
        //   about the sign, just the bits)
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.LongToBytes(bigDecimalValue.longValue()));

        // Convert the script to a real script
        scriptPubKey = JsonScriptConverter.convertOpcodeStringsToBytes(scriptPubKey);

        // Get the script data
        Byte[] scriptBytes = JsonHelper.byteStringToForwardBytes(scriptPubKey);

        // Add the script length
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes(scriptBytes.length));

        // Add the script
        ByteArrayHelper.addBytes(returnValue, scriptBytes);

        return returnValue.toArray(new Byte[returnValue.size()]);
    }
}
