package com.timmattison.bitcoin.test;

import com.google.gson.Gson;
import com.timmattison.bitcoin.test.script.json.JsonBlock;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */
public class ImportFromJson {
    public static Byte[] Import(String data) throws IllegalAccessException, InstantiationException, ParseException {
        if (data == null) {
            throw new UnsupportedOperationException("Data cannot be NULL");
        }

        Gson gson = new Gson();
        JsonBlock jsonBlock = gson.fromJson(data, JsonBlock.class);

        return jsonBlock.toBytes();
    }
}
