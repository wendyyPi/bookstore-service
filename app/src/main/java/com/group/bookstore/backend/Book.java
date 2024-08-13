package com.group.bookstore.backend;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Book {
    private int id;
    private String date;
    private String title;
    private String author;
    private String language;
    private String bookshelves;
    private int price;

    public Book(int id, String date, String title, String author, String languageCode, int price) {
        if(date == null || date.isEmpty()){
            throw new RuntimeException("Date should not be empty");
        }
        if (title == null || title.isEmpty()) {
            throw new RuntimeException("Title should not be empty");
        }
        if(author==null||author.isEmpty()){
            throw new RuntimeException("Author should not be empty");
        }
        if(languageCode == null || languageCode.isEmpty()){
            throw new RuntimeException("Language should not be empty");
        }
        this.id = id;
        this.date = date;
        this.title = title;
        this.author = author;
        this.language = languageCode;
        this.price = price;
    }

    public int getId() {
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getAuthor(){
        return author;
    }
    public String getLanguage(){
        return language;
    }
    public int getPrice(){
        return price;
    }
    public String getDate(){
        return date;
    }
    public String getBookshelves(){
        return bookshelves;
    }
    @Override
    public String toString() {
        return title + " by " + author + " in "+ date + " price at " + price;
    }

}
