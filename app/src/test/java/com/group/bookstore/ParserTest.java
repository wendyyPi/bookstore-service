package com.group.bookstore;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.fail;


import com.group.bookstore.backend.tokenizer.Tokenizer;
import com.group.bookstore.backend.parser.Exp;
import com.group.bookstore.backend.parser.Parser;

import org.junit.Test;

public class ParserTest {
    private static Tokenizer tokenizer;
    private Parser parser;
    private static final String SIMPLE_CASE1 = "@tom";
    private static final String SIMPLE_CASE2 = "#java";
    private static final String COMPLEX_CASE1 = "@tom;#java";
    private static final String COMPLEX_CASE2 = "#java;@jerry;#program";


    @Test(timeout=1000)
    public void testSimpleCase1(){
        tokenizer = new Tokenizer(SIMPLE_CASE1);
        try{
            Exp exp = new Parser(tokenizer).parseExp();
            assertEquals("incorrect display format", "author: tom", exp.show());
        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test(timeout=1000)
    public void testSimpleCase2(){
        tokenizer = new Tokenizer(SIMPLE_CASE2);
        try{
            Exp exp = new Parser(tokenizer).parseExp();
            assertEquals("incorrect display format", "title: java", exp.show());
        }catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test(timeout=1000)
    public void testComplexCase1(){
        tokenizer = new Tokenizer(COMPLEX_CASE1);
        try{
            Exp exp = new Parser(tokenizer).parseExp();
            assertEquals("incorrect display format", "author: tom AND title: java", exp.show());
        }catch (Exception e){
            fail(e.getMessage());
        }


    }
    @Test(timeout=1000)
    public void testComplexCase2() {
        tokenizer = new Tokenizer(COMPLEX_CASE2);
        try {
            Exp exp = new Parser(tokenizer).parseExp();
            assertEquals("incorrect display format", "title: java AND author: jerry AND title: program", exp.show());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test(timeout = 1000)
    public void testNoSymbolToken() {
        tokenizer = new Tokenizer("programming");
        try {
            Exp exp = new Parser(tokenizer).parseExp();
            assertEquals("incorrect display format", "title: programming", exp.show());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @Test(timeout=1000)
    public void testParseAlpha() {
        tokenizer = new Tokenizer("a");
        parser = new Parser(tokenizer);
        assertEquals('a', parser.parseAlpha());

        tokenizer = new Tokenizer("Z");
        parser = new Parser(tokenizer);
        assertEquals('Z', parser.parseAlpha());
    }


    @Test
    public void testIllegalTokenException() {
        Tokenizer tokenizer = new Tokenizer("");
        Parser parser = new Parser(tokenizer);
        assertThrows(Parser.IllegalProductionException.class, parser::parseExp);
    }

    }
