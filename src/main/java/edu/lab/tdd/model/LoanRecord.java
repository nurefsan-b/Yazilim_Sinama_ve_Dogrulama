package edu.lab.tdd.model;

import java.time.LocalDate;

public class LoanRecord {
    private final Book book;
    private final Member member;
    private final LocalDate borrowDate;
    private LocalDate returnDate;

    public LoanRecord(Book book,Member member,LocalDate borrowDate){
        this.book=book;
        this.member=member;
        this.borrowDate=borrowDate;
    }
    public Book getBook(){
        return book;
    }
    public Member getMember(){
        return member;
    }
    public LocalDate getBorrowDate(){
        return borrowDate;
    }
    public LocalDate getReturnDate(){
        return returnDate;
    }
    public void setReturnDate(LocalDate d){
        this.returnDate=d;
    }
}
