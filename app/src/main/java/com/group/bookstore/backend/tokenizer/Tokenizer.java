package com.group.bookstore.backend.tokenizer;
/**
 * This class is for tokenize text
 * @author Wenyi Pi
 */
public class Tokenizer {
    private String buffer;
    private Token currentToken;

    public Tokenizer(String text) {
        buffer = text;
        next();
    }
    public void next() {
        buffer = buffer.trim();

        if (buffer.isEmpty()) {
            currentToken = null;
            return;
        }

        char firstChar = buffer.charAt(0);

        // Semicolons
        if (firstChar == ';') {
            currentToken = new Token(";", Token.Type.AND);
            buffer = buffer.substring(1);
            return;
        }

        // For Title
        if (firstChar == '#') {
            int endOfText = buffer.indexOf(";");
            int authorIndex = buffer.indexOf("@");
            if (authorIndex != -1 && (endOfText == -1 || authorIndex < endOfText)) {
                endOfText = authorIndex;
            } else if (endOfText == -1) {
                endOfText = buffer.length();
            }

            String text = buffer.substring(1, endOfText).trim();
            currentToken = new Token(text, Token.Type.TITLE);
            buffer = buffer.substring(endOfText);

        }

        // For Author
        else if (firstChar == '@') {
            int endOfText = buffer.indexOf(";");
            int titleIndex = buffer.indexOf("#");
            if (titleIndex != -1 && (endOfText == -1 || titleIndex < endOfText)) {
                endOfText = titleIndex;
            } else if (endOfText == -1) {
                endOfText = buffer.length();
            }

            String text = buffer.substring(1, endOfText).trim();
            currentToken = new Token(text, Token.Type.AUTHOR);
            buffer = buffer.substring(endOfText);

        }

        // If no symbol found, consider the text as a title, for search invalid
        else {
            currentToken = new Token(buffer, Token.Type.TITLE);
            buffer = "";
        }

    }
    public Token current() {
        return currentToken;
    }

    public boolean hasNext() {
        return currentToken != null;
    }
}

