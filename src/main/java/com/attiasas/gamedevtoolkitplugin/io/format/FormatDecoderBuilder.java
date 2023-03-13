package com.attiasas.gamedevtoolkitplugin.io.format;

/**
 * @Author: Assaf, On 3/13/2023
 * @Description:
 **/
public class FormatDecoderBuilder {
    public String ObjectStartSymbol = "{";
    public String ObjectEndSymbol = "}";
    public String ArrayStartSymbol = "[";
    public String ArrayEndSymbol = "]";
    public String DelimiterSymbol = ",";
    public String AttributeAssignSymbol = "=";
    public String StringSymbol = "\"";
    public String CommentSymbol = "#";

    public FormatDecoderBuilder() {}

    public FormatDecoderBuilder(String objectStartSymbol, String objectEndSymbol, String arrayStartSymbol, String arrayEndSymbol, String delimiterSymbol, String attributeAssignSymbol, String stringSymbol, String commentSymbol) {
        ObjectStartSymbol = objectStartSymbol;
        ObjectEndSymbol = objectEndSymbol;
        ArrayStartSymbol = arrayStartSymbol;
        ArrayEndSymbol = arrayEndSymbol;
        DelimiterSymbol = delimiterSymbol;
        AttributeAssignSymbol = attributeAssignSymbol;
        StringSymbol = stringSymbol;
        CommentSymbol = commentSymbol;
    }

    public FormatDecoderBuilder setObjectStartSymbol(String objectStartSymbol) {
        ObjectStartSymbol = objectStartSymbol;
        return this;
    }

    public FormatDecoderBuilder setObjectEndSymbol(String objectEndSymbol) {
        ObjectEndSymbol = objectEndSymbol;
        return this;
    }

    public FormatDecoderBuilder setArrayStartSymbol(String arrayStartSymbol) {
        ArrayStartSymbol = arrayStartSymbol;
        return this;
    }

    public FormatDecoderBuilder setArrayEndSymbol(String arrayEndSymbol) {
        ArrayEndSymbol = arrayEndSymbol;
        return this;
    }

    public FormatDecoderBuilder setDelimiterSymbol(String delimiterSymbol) {
        DelimiterSymbol = delimiterSymbol;
        return this;
    }

    public FormatDecoderBuilder setAttributeAssignSymbol(String attributeAssignSymbol) {
        AttributeAssignSymbol = attributeAssignSymbol;
        return this;
    }

    public FormatDecoderBuilder setStringSymbol(String stringSymbol) {
        StringSymbol = stringSymbol;
        return this;
    }

    public FormatDecoderBuilder setCommentSymbol(String commentSymbol) {
        CommentSymbol = commentSymbol;
        return this;
    }
}
