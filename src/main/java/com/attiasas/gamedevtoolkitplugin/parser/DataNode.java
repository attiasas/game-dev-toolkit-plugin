package com.attiasas.gamedevtoolkitplugin.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Assaf, On 2/28/2023
 * @Description:
 **/
public class DataNode {
    private List<Object> content = new ArrayList<>(); // single value or an ordered list of values
    private Map<String,DataNode> objectReferences = new HashMap<>(); // attribute indicator (name) -> object

    public DataNode get(String... indicators) {
        DataNode crawler = this;

        return crawler;
    }

    public String getString() {
        return get(0).toString();
    }

    public float getFloat() {
        return Float.parseFloat(getString());
    }

    public double getDouble() {
        return Double.parseDouble(getString());
    }

    public int getInt() {
        return Integer.parseInt(getString());
    }

    public Object get(int index) {
        return this.content.get(index);
    }
}
