package com.group.bookstore.backend.parser;

public class AndExp extends Exp {

    private Exp term;
    private Exp exp;


    public AndExp(Exp term, Exp exp) {
        this.term = term;
        this.exp = exp;
    }


    @Override
    public String show() {
        return term.show() + " AND " + exp.show();
    }


}
