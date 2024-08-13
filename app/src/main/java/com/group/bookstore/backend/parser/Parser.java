package com.group.bookstore.backend.parser;

import com.group.bookstore.backend.tokenizer.Token;
import com.group.bookstore.backend.tokenizer.Tokenizer;
/**
 * This class is to parser tokens
 * @author Wenyi Pi
 */
/** grammar:
 * <exp>       ::= <term> AND <exp> | <term>
 * <AND>       ::= ;
 * <term>      ::= <title> | <author>
 * <title>     ::= # <text>
 * <author>    ::= @ <text>
 * <text>      ::= <alpha> <text> | <alpha>
 * <alpha>     ::= a | b | c | d | ... | y | z
 * The possible formats for the query:
 * @author
 * #title
 * Any combination of the above separated by semicolons ;
 * "@author;#title" order can change
 * */
public class Parser {
    Tokenizer tokenizer;

    public Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }


    public static class IllegalProductionException extends IllegalArgumentException {
        public IllegalProductionException(String errorMessage) {
            super(errorMessage);
        }
    }

    // <exp> ::= <term> AND <exp> | <term>
    public Exp parseExp() {
        Exp term = parseTerm();

        if (tokenizer.hasNext() && tokenizer.current().getType() == Token.Type.AND) {
            tokenizer.next();
            Exp nextExp = parseExp();
            return new AndExp(term, nextExp);
        }

        return term;
    }

    // <term> ::= <title> | <author>
    public Exp parseTerm() {
        if (!tokenizer.hasNext()) {
            throw new IllegalProductionException("Expected TERM");
        }

        Token.Type type = tokenizer.current().getType();
        if (type == Token.Type.TITLE) {
            return parseTitle();
        } else if (type == Token.Type.AUTHOR) {
            return parseAuthor();
        } else {
            throw new IllegalProductionException("Unexpected token type: " + tokenizer.current().getType());
        }
    }

    // <title> ::= # <text>
    private TitleExp parseTitle() {
        if (tokenizer.current().getType() != Token.Type.TITLE) {
            throw new IllegalProductionException("Expected TITLE");
        }
        String title = tokenizer.current().getToken();
        tokenizer.next();
        return new TitleExp(title);
    }

    // <author> ::= @ <text>
    private AuthorExp parseAuthor() {
        if (tokenizer.current().getType() != Token.Type.AUTHOR) {
            throw new IllegalProductionException("Expected AUTHOR");
        }
        String author = tokenizer.current().getToken();
        tokenizer.next();
        return new AuthorExp(author);
    }


    //<alpha> ::= a | b | c | d | ... | y | z
    public char parseAlpha() {
        if (!tokenizer.hasNext() || !isAlpha(tokenizer.current().getToken())) {
            throw new IllegalProductionException("Expected ALPHA");
        }
        char alpha = tokenizer.current().getToken().charAt(0);
        tokenizer.next();
        return alpha;
    }
    private boolean isAlpha(String token) {
        if (token.length() != 1) return false;
        char ch = token.charAt(0);
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z');
    }

}



