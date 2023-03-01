package com.attiasas.gamedevtoolkitplugin.language.parser;

import com.attiasas.gamedevtoolkitplugin.language.DataNode;
import com.intellij.model.Symbol;

import java.util.List;

/**
 * @Author: Assaf, On 3/2/2023
 * @Description:
 **/
public class ParseUniOperation implements DataParseRule {

    private Symbol operationSymbol;
    private String operationToken;
    private boolean symbolToLeft;

    public ParseUniOperation(Symbol operationSymbol, String operationToken, boolean symbolToLeft) {
        this.operationSymbol = operationSymbol;
        this.operationToken = operationToken;
        this.symbolToLeft = symbolToLeft;
    }

    public ParseUniOperation(Symbol operationSymbol, String operationToken) {
        this(operationSymbol,operationToken,true);
    }

    public ParseUniOperation(String operationToken, Symbol operationSymbol) {
        this(operationSymbol,operationToken,false);
    }

    @Override
    public int match(int currentIndex, List<String> tokens) {
        return 0;
    }

    @Override
    public DataNode parse(DataNode current, int currentIndex, List<String> tokens) {
        return null;
    }
}
