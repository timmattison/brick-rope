package com.timmattison.cryptocurrency.bitcoin;

/**
 * Created by timmattison on 9/29/14.
 */
public class BasicWordDumper implements WordDumper {
    @Override
    public String prettyDump(int indentationLevel, Word word) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n");

        for (int loop = 0; loop < indentationLevel; loop++) {
            stringBuilder.append("\t");
        }

        stringBuilder.append(word.getName());
        stringBuilder.append(": ");
        stringBuilder.append(String.format(", 0x%02x", word.getOpcode()));

        return stringBuilder.toString();
    }
}
