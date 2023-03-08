package com.attiasas.gamedevtoolkitplugin.io.format;

import com.attiasas.gamedevtoolkitplugin.io.DataNode;
import com.attiasas.gamedevtoolkitplugin.parser.Tokenizer;

import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class GDFormat implements IFormat<DataNode> {

    // Attributes indicator definition - at least one alphanumeric char and _.- also allowed
    private static final String SpecialAllowed = "_.-";
    public static final String  IndicatorPattern = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9" + SpecialAllowed + "]+$";

    public static final String ObjectStartSymbol = "{";
    public static final String ObjectEndSymbol = "}";
    public static final String ArrayStartSymbol = "[";
    public static final String ArrayEndSymbol = "]";
    public static final String ArrayDelimiterSymbol = ",";
    public static final String AttributeAssignSymbol = "=";
    public static final String StringSymbol = "\"";
    public static final String CommentSymbol = "#";

    private static final String EndLine = "\n";

    @Override
    public DataNode parse(String input) throws FormatException {
        List<Tokenizer.Token> tokens = filterNotImportantTokens(new Tokenizer("\\s+",EndLine).tokenize(input));

        Stack<DataNode> parsingObjStack = new Stack<>();
        DataNode root = new DataNode("DataObject");
        parsingObjStack.push(root);

        // Parse all the input tokens
        int currentIndex = 0;
        try {
            int parsedTokens = parseObjectContent(parsingObjStack, 0, tokens);

            if (parsedTokens < tokens.size()) {
                Exception error = new Exception("Parsing ended prematurely");
                if (parsingObjStack.size() > 1) {
                    error = new Exception("Expected '" + ObjectEndSymbol + "' but got '" + tokens.get(currentIndex) + "'");
                }
                throw error;
            }
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
        return root;
    }

    private FormatException getFormatException(Tokenizer.Token token, Throwable cause) {
        return  (cause instanceof FormatException) ? (FormatException)cause : new FormatException(token, cause);
    }

    private List<Tokenizer.Token> filterNotImportantTokens(List<Tokenizer.Token> tokens) {
        // Filter all the delimiters except the end-line symbol
        return tokens.stream().filter(token -> isEndLineOrComment(token) || !token.token.matches("\\s+")).collect(Collectors.toList());
    }

    private int parseObjectContent(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            while (!isEndObject(currentIndex, tokens)) {
                int advance = parseCommentsAndEndLines(currentIndex, tokens);
                advance += parseAttribute(dataNodeStack, currentIndex + advance, tokens);
                if (advance == 0) {
                    break;
                }
                currentIndex += advance;
            }
            if (isEndObject(currentIndex, tokens)) {
                currentIndex += parseEndObj(dataNodeStack, currentIndex, tokens);
            }
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
    }

    private boolean isEndObject(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && !tokens.get(currentIndex).token.equals(ObjectEndSymbol);
    }

    private int parseCommentsAndEndLines(int startIndex, List<Tokenizer.Token> tokens) {
        int currentIndex = startIndex;
        while (currentIndex < tokens.size() && isEndLineOrComment(tokens.get(currentIndex))) {
            currentIndex++;
        }
        return currentIndex - startIndex;
    }

    private boolean isEndLineOrComment(Tokenizer.Token token) {
        return token.token.equals(EndLine);
    }

    private int parseEndObj(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        Tokenizer.Token token = tokens.get(startIndex);
        try {
            if (token.equals(ObjectEndSymbol)) {
                if (dataNodeStack.size() == 1) {
                    throw new Exception("Expected attribute/value definition but got '" + ObjectEndSymbol + "'");
                }
                dataNodeStack.pop();
                return 1;
            }
            return 0;
        } catch (Exception e) {
            throw getFormatException(token,e);
        }

    }

    private int parseAttribute(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            Tokenizer.Token indicator = tokens.get(currentIndex);
            if (!indicator.token.matches(IndicatorPattern)) {
                throw new Exception("Expected alphanumeric (special chars ['_','-','.'] allowed) attribute indicator but got '" + indicator.token + "'");
            } else if (dataNodeStack.peek().has(indicator.token)) {
                throw new Exception("Attribute '" + indicator.token + "' already defined at object '" + dataNodeStack.peek().indicator() + "'")
            }
            currentIndex++;
            currentIndex += parseValue(dataNodeStack, indicator, currentIndex, tokens, 0);
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
    }

    private int parseValue(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int startIndex, List<Tokenizer.Token> tokens, int i) throws FormatException {
        int currentIndex = startIndex;
        try {
            if (currentIndex >= tokens.size() || isEndLineOrComment(tokens.get(currentIndex))) {
                currentIndex += parseEmptyIndicator(dataNodeStack, indicator, currentIndex, tokens, i);
                return startIndex - currentIndex;
            }
            Tokenizer.Token symbol = tokens.get(currentIndex);
            if (symbol.equals(ObjectStartSymbol)) {
                // new DataNode object attribute
                DataNode obj = null;
                if (i == 0) {
                    obj = dataNodeStack.peek().get(indicator.token);
                } else {

                }
                dataNodeStack.push(obj);
                currentIndex++;
            } else if (symbol.token.equals(AttributeAssignSymbol)) {
                currentIndex++;
                int valueCount = 0;
                if (tokens.get(currentIndex).token.equals(ArrayStartSymbol)) {
                    currentIndex++;
                    // array attribute value or empty list
                    while (!isEndArray(currentIndex, tokens)) {
                        currentIndex += parseValue(dataNodeStack, indicator, currentIndex, tokens, valueCount);
                        if (tokens.get(currentIndex).token.equals())
                        valueCount++;
                    }
                    if (isEndArray(currentIndex, tokens)) {
                        currentIndex++;
                        if (valueCount == 0) {
                            // empty list is allowed
                            currentIndex += parseEmptyIndicator(dataNodeStack, indicator, currentIndex, tokens, 0);
                            return startIndex - currentIndex;
                        }
                    }
                } else {
                    // Primitive attribute value
                    valueCount = parsePrimitiveValue(dataNodeStack, indicator, currentIndex, tokens);
                    currentIndex += valueCount;
                }
                if (valueCount == 0) {
                    throw new Exception("Expected at least one value after assignment symbol");
                }
            } else {
                throw new Exception("Expected '" + AttributeAssignSymbol + "' or '" + ObjectStartSymbol + "' but got " + symbol.token);
            }
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
    }

    private int parseEmptyIndicator(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int currentIndex, List<Tokenizer.Token> tokens) {
        // Empty indicator attribute: construct empty data node attribute for this indicator
        dataNodeStack.peek().get(indicator.token);
        return parseCommentsAndEndLines(currentIndex, tokens);
    }

    private int parsePrimitiveValue(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int currentIndex, List<Tokenizer.Token> tokens, int i) {

    }

    private int parsePrimitiveValue(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int currentIndex, List<Tokenizer.Token> tokens) {
        return parsePrimitiveValue(dataNodeStack, indicator, currentIndex, tokens, 0);
    }

    private boolean isEndArray(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && !tokens.get(currentIndex).token.equals(ArrayEndSymbol);
    }

    @Override
    public String toString(DataNode data) {
        return null;
    }

    public static void main(String[] args) {
        GDFormat format = new GDFormat();
        try {
            DataNode data = format.parse(Paths.get("src/test/resources/testGDFormat.gdis"));
            System.out.println("Data:\n" + data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
