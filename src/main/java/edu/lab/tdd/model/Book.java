package edu.lab.tdd.model;

public class Book {
    private final String isbn;
    private final String title;
    private boolean isAvailable;

    public Book(String isbn,String title,boolean isAvailable){
        this.isbn=isbn;
        this.title=title;
        this.isAvailable=isAvailable;
    }

    public String getIsbn(){
        return isbn;
    }
    public String getTitle(){
        return title;
    }
    public boolean isAvailable(){
        return isAvailable;
    }

    public void setIsAvailable(boolean isAvailable){
        this.isAvailable=isAvailable;
    }


}
