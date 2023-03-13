package com.attiasas.gamedevtoolkitplugin.io.format;

import com.attiasas.gamedevtoolkitplugin.io.DataNode;
import com.attiasas.gamedevtoolkitplugin.io.Tokenizer;
import com.attiasas.gamedevtoolkitplugin.utils.RegexBuilder;

import java.nio.file.Paths;
import java.util.List;
import java.util.Stack;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @Author: Assaf, On 3/1/2023
 * @Description:
 **/
public class GDFormat implements IFormat<DataNode> {

    // Attributes indicator definition - at least one alphanumeric char and _.- also allowed
    private static final String SpecialAllowed = "_.-";
    public static final String  IndicatorPattern = "^(?=.*[a-zA-Z0-9])[a-zA-Z0-9" + SpecialAllowed + "]+$";
    private static final String EndLine = "\n";

    public final String ObjectStartSymbol;
    public final String ObjectEndSymbol;
    public final String ArrayStartSymbol;
    public final String ArrayEndSymbol;
    public final String DelimiterSymbol;
    public final String AttributeAssignSymbol;
    public final String StringSymbol;
    public final String CommentSymbol;

    public GDFormat(FormatDecoderBuilder format) {
        ObjectStartSymbol = format.ObjectStartSymbol;
        ObjectEndSymbol = format.ObjectEndSymbol;
        ArrayStartSymbol = format.ArrayStartSymbol;
        ArrayEndSymbol = format.ArrayEndSymbol;
        DelimiterSymbol = format.DelimiterSymbol;
        AttributeAssignSymbol = format.AttributeAssignSymbol;
        StringSymbol = format.StringSymbol;
        CommentSymbol = format.CommentSymbol;
    }

    public GDFormat() {
        this(new FormatDecoderBuilder());
    }

    @Override
    public DataNode parse(String input) throws FormatException {
        List<Tokenizer.Token> tokens = filterNotImportantTokens(new Tokenizer(EndLine,RegexBuilder.WHITESPACE_EXCEPT_NEWLINE, RegexBuilder.matchBetweenWithEscaping(StringSymbol), Pattern.quote(ObjectStartSymbol), Pattern.quote(ObjectEndSymbol), Pattern.quote(ArrayStartSymbol), Pattern.quote(ArrayEndSymbol), Pattern.quote(AttributeAssignSymbol), Pattern.quote(DelimiterSymbol)).tokenize(input));

        Stack<DataNode> parsingObjStack = new Stack<>();
        DataNode root = new DataNode("DataObject");
        parsingObjStack.push(root);

        // Parse all the input tokens
        int currentIndex = 0;
        try {
            int parsedTokens = parseObjectContent(parsingObjStack, 0, tokens);

            if (parsedTokens < tokens.size() || parsingObjStack.size() > 1) {
                Exception error = new Exception("Parsing ended prematurely");
                if (parsingObjStack.size() > 1) {
                    error = new Exception("Expected '" + ObjectEndSymbol + "' but got '" + tokens.get(currentIndex).token + "'");
                }
                throw error;
            }
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
        return root;
    }

    private List<Tokenizer.Token> filterNotImportantTokens(List<Tokenizer.Token> tokens) {
        // Filter all the delimiters except the end-line symbol
        return tokens.stream().filter(token -> !token.token.matches(RegexBuilder.WHITESPACE_EXCEPT_NEWLINE)).collect(Collectors.toList());
    }

    private FormatException getFormatException(Tokenizer.Token token, Throwable cause) {
        return (cause instanceof FormatException) ? (FormatException)cause : new FormatException(token, cause);
    }

    private int parseObjectContent(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            while (currentIndex < tokens.size() && !isEndObject(currentIndex, tokens)) {
                // Parse delimiter if needed
                currentIndex += dataNodeStack.peek().size() > 0 ? parseDelimiter(currentIndex, tokens) : parseEndLines(currentIndex, tokens);
                currentIndex += parseEndLines(currentIndex, tokens);
                if (currentIndex >= tokens.size() || isEndObject(currentIndex, tokens)) {
                    break;
                }
                // Parse attribute
                currentIndex += parseAttribute(dataNodeStack, currentIndex, tokens);
            }
            if (isEndObject(currentIndex, tokens)) {
                currentIndex += parseEndObj(dataNodeStack, currentIndex, tokens);
            }
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
    }

    private boolean isEndLine(Tokenizer.Token token) {
        return token.token.equals(EndLine);
    }

    private int parseEndLines(int startIndex, List<Tokenizer.Token> tokens) {
        int currentIndex = startIndex;
        while (currentIndex < tokens.size() && isEndLine(tokens.get(currentIndex))) {
            currentIndex++;
        }
        return currentIndex - startIndex;
    }

    private boolean isStartObject(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && tokens.get(currentIndex).token.equals(ObjectStartSymbol);
    }

    private boolean isEndObject(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && tokens.get(currentIndex).token.equals(ObjectEndSymbol);
    }

    private int parseEndObj(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        try {
            if (isEndObject(startIndex, tokens)) {
                if (dataNodeStack.size() == 1) {
                    throw new Exception("Expected attribute/value definition but got '" + ObjectEndSymbol + "'");
                }
                dataNodeStack.pop();
                return 1;
            }
            return 0;
        } catch (Exception e) {
            throw getFormatException(tokens.get(startIndex),e);
        }
    }

    private int parseDelimiter(int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int advance = 0;
        try {
            advance = parseEndLines(startIndex, tokens);
            if (tokens.get(startIndex + advance).token.equals(DelimiterSymbol)) {
                advance++;
            }
            if (advance == 0) {
                throw new Exception("Expected delimiter but got '" + tokens.get(startIndex + advance).token + "'");
            }
            return advance;
        } catch (Exception e) {
            throw getFormatException(tokens.get(startIndex + advance),e);
        }
    }

    private int parseAttribute(Stack<DataNode> dataNodeStack, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            Tokenizer.Token indicator = tokens.get(currentIndex);
            if (!indicator.token.matches(IndicatorPattern)) {
                throw new Exception("Expected alphanumeric (special chars ['_','-','.'] allowed) attribute indicator but got '" + indicator.token + "'");
            } else if (dataNodeStack.peek().has(indicator.token)) {
                throw new Exception("Attribute '" + indicator.token + "' already defined at object '" + dataNodeStack.peek().indicator() + "'");
            }
            currentIndex++;
            if (currentIndex >= tokens.size() || isEndLine(tokens.get(currentIndex))) {
                if (currentIndex < tokens.size()) {
                    currentIndex++;
                }
                // "Null" / empty attribute
                dataNodeStack.peek().get(indicator.token);
                return currentIndex - startIndex;
            }
            Tokenizer.Token symbol = tokens.get(currentIndex);
            if (!isAssignSymbol(currentIndex, tokens) && !isStartObject(currentIndex, tokens)) {
                throw new Exception("Expected '" + AttributeAssignSymbol + "' or '" + ObjectStartSymbol + "' but got '" + symbol.token + "'");
            }
            if (isAssignSymbol(currentIndex, tokens)) {
                currentIndex++;
            }
            currentIndex += parseAttributeValues(dataNodeStack, indicator, currentIndex, tokens);
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex),e);
        }
    }

    private int parseEmptyIndicator(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int currentIndex, List<Tokenizer.Token> tokens) {
        // Empty indicator attribute: construct empty data node attribute for this indicator
        dataNodeStack.peek().get(indicator.token);
        return parseEndLines(currentIndex, tokens);
    }

    private boolean isAssignSymbol(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && tokens.get(currentIndex).token.equals(AttributeAssignSymbol);
    }

    private int parseAttributeValues(Stack<DataNode> dataNodeStack, Tokenizer.Token indicator, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            DataNode valueNode = dataNodeStack.peek().get(indicator.token);
            if (isStartObject(currentIndex, tokens)) {
                currentIndex++;
                // Object value
                dataNodeStack.push(valueNode);
                currentIndex += parseObjectContent(dataNodeStack, currentIndex, tokens);
                return currentIndex - startIndex;
            }
            if (isStartArray(currentIndex, tokens)) {
                currentIndex++;
                // Array value
                currentIndex += parseArrayValues(dataNodeStack, valueNode, currentIndex, tokens);
                return currentIndex - startIndex;
            }
            // Single primitive value
            currentIndex += parseValue(dataNodeStack, valueNode, currentIndex, tokens);
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex), e);
        }
    }

    private boolean isStartArray(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && tokens.get(currentIndex).token.equals(ArrayStartSymbol);
    }

    private boolean isEndArray(int currentIndex, List<Tokenizer.Token> tokens) {
        return currentIndex < tokens.size() && tokens.get(currentIndex).token.equals(ArrayEndSymbol);
    }

    private int parseArrayValues(Stack<DataNode> dataNodeStack, DataNode valueNode, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            while (currentIndex < tokens.size() && !isEndArray(currentIndex, tokens)) {
                currentIndex += valueNode.size() > 0 ? parseDelimiter(currentIndex, tokens) : parseEndLines(currentIndex, tokens);
                currentIndex += parseEndLines(currentIndex, tokens);
                if (currentIndex >= tokens.size() || isEndArray(currentIndex, tokens)) {
                    break;
                }
                currentIndex += parseValue(dataNodeStack, valueNode, currentIndex, tokens);
            }
            if (!isEndArray(currentIndex, tokens)) {
                throw new Exception("Expected end array symbol '" + ArrayEndSymbol + "'");
            }
            if (currentIndex < tokens.size()) {
                currentIndex++;
            }
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex), e);
        }
    }

    private int parseValue(Stack<DataNode> dataNodeStack, DataNode valueNode, int startIndex, List<Tokenizer.Token> tokens) throws FormatException {
        int currentIndex = startIndex;
        try {
            boolean isStartObject = isStartObject(currentIndex,tokens);
            if (isStartObject || isStartArray(currentIndex, tokens)) {
                currentIndex++;
                // anonymous object or array inside an array
                String anonymousIndicator = valueNode.indicator() + valueNode.size();
                DataNode anonymousNode = new DataNode(anonymousIndicator);
                valueNode.add(anonymousNode);
                if (isStartObject) {
                    dataNodeStack.push(anonymousNode);
                    currentIndex += parseObjectContent(dataNodeStack, currentIndex, tokens);
                } else {
                    currentIndex += parseArrayValues(dataNodeStack, anonymousNode, currentIndex, tokens);
                }
                return currentIndex - startIndex;
            }
            if (tokens.get(currentIndex).token.startsWith(StringSymbol) && tokens.get(currentIndex).token.endsWith(StringSymbol)) {
                valueNode.add(tokens.get(currentIndex).token.substring(1,tokens.get(currentIndex).token.length() - 1));
            } else if (tokens.get(currentIndex).token.equals(RegexBuilder.INTEGER)) {
                valueNode.add(Integer.parseInt(tokens.get(currentIndex).token));
            } else if (tokens.get(currentIndex).token.equals(RegexBuilder.NUMBER)) {
                valueNode.add(Float.parseFloat(tokens.get(currentIndex).token));
            } else {
                // Default string value
                valueNode.add(tokens.get(currentIndex).token);
            }
            currentIndex++;
            return currentIndex - startIndex;
        } catch (Exception e) {
            throw getFormatException(tokens.get(currentIndex), e);
        }
    }

    @Override
    public String toString(DataNode data) {
        return DataNode.writeAsString(data,0,AttributeAssignSymbol,"\t"," ",DelimiterSymbol,StringSymbol,ObjectStartSymbol,ObjectEndSymbol, ArrayStartSymbol, ArrayEndSymbol,EndLine);
    }

    public static void main(String[] args) {
        GDFormat format = new GDFormat();
        try {
            DataNode data = format.parse(Paths.get("src/test/resources/testGDFormat.gdis")); // testGradeBuildFormat.gradle"));//
            System.out.println("Data:\n" + format.toString(data));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
