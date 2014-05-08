package com.timmattison.cryptocurrency.standard.interfaces;

import com.timmattison.cryptocurrency.interfaces.Buildable;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 4/16/13
 * Time: 10:22 AM
 * To change this template use File | Settings | File Templates.
 */
public interface VariableLengthInteger extends Buildable {
    public void setValue(Long value);

    public byte[] build(byte[] data);

    public long getValue();

    public byte[] getValueBytes();
}
