package com.group.bookstore.backend.parser;

public class AuthorExp extends Exp {
    private String author;

    public AuthorExp(String author) {
        this.author = author;
    }

    @Override
    public String show() {
        return "author: " + author;
    }

}
