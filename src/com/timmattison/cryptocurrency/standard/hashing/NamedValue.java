package com.timmattison.cryptocurrency.standard.hashing;

/**
 * Created with IntelliJ IDEA.
 * User: timmattison
 * Date: 5/16/13
 * Time: 7:08 AM
 * To change this template use File | Settings | File Templates.
 */
public class NamedValue<T> {
    private String name;
    private T value;

    public NamedValue(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public String getHexValue() {
        if (value instanceof Integer) {
            return Integer.toHexString((Integer) value);
        } else if (value instanceof Long) {
            return Long.toHexString((Long) value);
        } else {
            return "N/A";
        }
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }
}
