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
 * Time: 8:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class JsonBlock {
    private static final Byte[] requiredMagicNumberBytes = new Byte[]{new Byte((byte) 0xf9), new Byte((byte) 0xbe), new Byte((byte) 0xb4), new Byte((byte) 0xd9)};
    String hash;
    long ver;
    String prev_block;
    String mrkl_root;
    long time;
    long bits;
    long nonce;
    long n_tx;
    long size;
    List<JsonTx> tx;
    List<String> mkrl_tree;

    public Byte[] toBytes() throws InstantiationException, IllegalAccessException, ParseException {
        List<Byte> returnValue = new ArrayList<Byte>();

        // Add the magic number
        ByteArrayHelper.addBytes(returnValue, requiredMagicNumberBytes);

        // Add the block size
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) size));

        // Add the version
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) ver));

        // Add the previous block hash
        ByteArrayHelper.addBytes(returnValue, JsonHelper.byteStringToReversedBytes(prev_block));

        // Add the Merkle root
        ByteArrayHelper.addBytes(returnValue, JsonHelper.byteStringToReversedBytes(mrkl_root));

        // Add the timestamp
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) time));

        // Add the bits
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) bits));

        // Add the nonce
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.IntToBytes((int) nonce));

        // Add the transaction count
        ByteArrayHelper.addBytes(returnValue, EndiannessHelper.VarIntToBytes((int) n_tx));

        // Add the transactions
        for (JsonTx jsonTx : tx) {
            ByteArrayHelper.addBytes(returnValue, jsonTx.toBytes());
        }

        return returnValue.toArray(new Byte[returnValue.size()]);
    }
}

