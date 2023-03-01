package com.attiasas.gamedevtoolkitplugin.language.parser;

import com.attiasas.gamedevtoolkitplugin.language.DataNode;

import java.util.List;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public interface DataParseRule {

    public int match(int currentIndex, List<String> tokens);
    public DataNode parse(DataNode current, int currentIndex, List<String> tokens);
}
