package com.group.bookstore.backend.tokenizer;
/**
 * This class is for token
 * @author Wenyi Pi
 */
/**
 * Query Token Example:
 * "#java;@john"
 * Result:
 * TITLE(java)
 * AND(;)
 * AUTHOR(john)
 * */
public class Token {
    public enum Type {TITLE,AUTHOR,AND}

    public static class IllegalTokenException extends IllegalArgumentException {
        public IllegalTokenException(String errorMessage) {
            super(errorMessage);
        }
    }

    private final String token;
    private final Type type;


    //Constructor
    public Token(String token, Type type){
        this.token = token;
        this.type = type;
    }


    public String getToken(){
        return token;
    }

    public Type getType(){
        return type;
    }

    public String toString(){
        return type +"("+ token+ ")";
    }
}

