package com.group.bookstore.backend.parser;

public class TitleExp extends Exp {
    private String title;

    public TitleExp(String title) {

        this.title = title;
    }

    @Override
    public String show() {
        return "title: " + title;
    }


}


