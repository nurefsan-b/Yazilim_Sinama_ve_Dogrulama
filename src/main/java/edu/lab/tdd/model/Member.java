package edu.lab.tdd.model;

import java.util.ArrayList;
import java.util.List;

public class Member {
    private final String memberId;
    private final String name;
    private boolean active;
    private final List<LoanRecord> loans=new ArrayList<>();

    public Member(String memberId,String name,boolean active){
        this.memberId=memberId;
        this.name=name;
        this.active=active;
    }
    public String getMemberId(){
        return memberId;
    }
    public String getName(){
        return name;
    }
    public boolean isActive(){
        return active;
    }
    public List<LoanRecord>getLoans(){
        return loans;
    }
    public int getActiveLoanCount(){
        return(int) loans.stream().filter(l->l.getReturnDate()==null).count();
    }
}
