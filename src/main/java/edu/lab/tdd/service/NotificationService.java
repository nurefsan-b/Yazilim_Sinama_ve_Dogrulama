package edu.lab.tdd.service;
import edu.lab.tdd.model.Member;

public interface NotificationService{
    void sendOverdueAlert(Member member,String bookTitle,long overdueDays);
    void sendBorrowConfirmation(Member member,String bookTitle);
    void sendReturnConfirmation(Member member, String bookTitle);
}
