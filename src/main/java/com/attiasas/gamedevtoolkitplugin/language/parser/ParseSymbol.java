package com.attiasas.gamedevtoolkitplugin.language.parser;

import com.attiasas.gamedevtoolkitplugin.language.DataNode;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description: symbols delimiter between tokens
 **/
public class ParseSymbol implements DataParseRule {
    private char symbol;

    private static final Map<Character,String> addToChars = getSpecialSymbols();

    public ParseSymbol(char symbol) {
        this.symbol = symbol;
    }

    @Override
    public int match(int currentIndex, List<String> tokens) {
        return tokens.get(currentIndex).equals("" + symbol) ? 1 : 0;
    }

    @Override
    public DataNode parse(DataNode current, int currentIndex, List<String> tokens) {
        // we don't need to update node for symbols
        return match(currentIndex,tokens) > 0 ? current : null;
    }

    private static Map<Character,String> getSpecialSymbols() {
        Map<Character,String> special = new Hashtable<>();
        special.put('.',"\\.");
        special.put('(',"\\(");
        special.put(')',"\\)");
        special.put('[',"\\[");
        special.put(']',"\\]");
        special.put('[',"\\[");
        return special;
    }

}
