package com.timmattison.bitcoin.test;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/1/13
 * Time: 12:30 PM
 * To change this template use File | Settings | File Templates.
 */
public class DumpHelper {
    public static void dump(StringBuilder stringBuilder, boolean pretty, String header, String footer, byte[] bytes) {
        if(pretty) {
            stringBuilder.append(header);
        }

        stringBuilder.append(ByteArrayHelper.formatArray(bytes));

        if(pretty) {
            stringBuilder.append(footer);
        }
    }
}
