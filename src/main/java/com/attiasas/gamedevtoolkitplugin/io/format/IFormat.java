package com.attiasas.gamedevtoolkitplugin.io.format;

import com.attiasas.gamedevtoolkitplugin.parser.Tokenizer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Author: Assaf, On 3/4/2023
 * @Description:
 **/
public interface IFormat<T> {
    public T parse(String input) throws FormatException;
    public default T parse(Path filePath) throws IOException, FormatException {
        return parse(new String(Files.readAllBytes(filePath)));
    }
    public String toString(T data) throws FormatException;

    public static class FormatException extends Exception {
        public final String token;
        public final int line;
        public final int position;

        public FormatException(String msg) {
            super(msg);
            this.token = "";
            this.line = 0;
            this.position = 0;
        }

        public FormatException(String token, int line, int position, Throwable cause) {
            super(getMsg(token, line, position) + ": " + cause.getMessage(), cause);
            this.token = token;
            this.line = line;
            this.position = position;
        }

        public FormatException(String token, int line, int position) {
            super(getMsg(token, line, position));
            this.token = token;
            this.line = line;
            this.position = position;
        }

        public FormatException(Tokenizer.Token token, Throwable cause) {
            this(token.token, token.line, token.position, cause);
        }

        public FormatException(Tokenizer.Token token) {
            this(token.token, token.line, token.position);
        }

        private static String getMsg(String token, int line, int position) {
            return "Found format issue at token '" + token + "' in [line=" + line + ", position=" + position + "]";
        }
    }
}
