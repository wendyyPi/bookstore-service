package com.group.bookstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import com.group.bookstore.backend.tokenizer.Token;
import com.group.bookstore.backend.tokenizer.Tokenizer;

import org.junit.Test;

public class TokenizerTest {
    private static Tokenizer tokenizer;
    private static final String AUTHOR_AND_TITLE = ";";
    private static final String AUTHOR = "@authorName";
    private static final String TITLE = "#bookTitle";

    private static final String RESULT1 = "#java;@jerry";
    private static final String RESULT2 = "@jerry;#java;#the";

    @Test(timeout = 1000)
    public void testAddToken() {
        tokenizer = new Tokenizer(AUTHOR_AND_TITLE);
        assertEquals("wrong token type", Token.Type.AND, tokenizer.current().getType());
        assertEquals("wrong token value", ";", tokenizer.current().getToken());
    }
    @Test(timeout = 1000)
    public void testAuthorToken() {
        tokenizer = new Tokenizer(AUTHOR);
        assertEquals("wrong token type", Token.Type.AUTHOR, tokenizer.current().getType());
        assertEquals("wrong token value", "authorName", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testTitleToken() {
        tokenizer = new Tokenizer(TITLE);
        assertEquals("wrong token type", Token.Type.TITLE, tokenizer.current().getType());
        assertEquals("wrong token value", "bookTitle", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testNoSymbolToken() {
        tokenizer = new Tokenizer("programming");
        assertEquals("wrong token type", Token.Type.TITLE, tokenizer.current().getType());
    }

    @Test(timeout = 1000)
    public void testResult1() {
        tokenizer = new Tokenizer(RESULT1);
        // test first token #java
        assertEquals("wrong token type for title", Token.Type.TITLE, tokenizer.current().getType());
        assertEquals("wrong token value for title", "java", tokenizer.current().getToken());

        // test second token ;
        tokenizer.next();
        assertEquals("wrong token type for AND", Token.Type.AND, tokenizer.current().getType());
        assertEquals("wrong token value for AND", ";", tokenizer.current().getToken());

        // test third token @jerry
        tokenizer.next();
        assertEquals("wrong token type for author", Token.Type.AUTHOR, tokenizer.current().getType());
        assertEquals("wrong token value for author", "jerry", tokenizer.current().getToken());
    }

    @Test(timeout = 1000)
    public void testResult2() {
        tokenizer = new Tokenizer(RESULT2);
        // test first token @jerry
        assertEquals("wrong token type for author", Token.Type.AUTHOR, tokenizer.current().getType());
        assertEquals("wrong token value for author", "jerry", tokenizer.current().getToken());

        // test second token ;
        tokenizer.next();
        assertEquals("wrong token type for AND", Token.Type.AND, tokenizer.current().getType());
        assertEquals("wrong token value for AND", ";", tokenizer.current().getToken());

        // test third token #java
        tokenizer.next();
        assertEquals("wrong token type for title", Token.Type.TITLE, tokenizer.current().getType());
        assertEquals("wrong token value for title", "java", tokenizer.current().getToken());

        // test fourth token ;
        tokenizer.next();
        assertEquals("wrong token type for AND", Token.Type.AND, tokenizer.current().getType());
        assertEquals("wrong token value for AND", ";", tokenizer.current().getToken());

        // test fifth token #the
        tokenizer.next();
        assertEquals("wrong token type for title", Token.Type.TITLE, tokenizer.current().getType());
        assertEquals("wrong token value for title", "the", tokenizer.current().getToken());
    }
    @Test
    public void testIllegalTokenException() {
        Token.IllegalTokenException exception = assertThrows(Token.IllegalTokenException.class, () -> {
            throw new Token.IllegalTokenException("Illegal Token Error");
        });
        assertEquals("Illegal Token Error", exception.getMessage());
    }
}
