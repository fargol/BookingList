package com.example.android.bookinglist;



public class ListBook {
    private String bookName;
    private String bookAuthor;
    public ListBook(String bookName,String bookAuthor){
        this.bookAuthor=bookAuthor;
        this.bookName=bookName;
    }
    public String getBookName(){return bookName;}
    public String getBookAuthor(){return bookAuthor;}
}
