package edu.lab.tdd.exception;

public class InactiveMemberException extends RuntimeException {
    public InactiveMemberException(String message) {
        super(message);
    }
}
