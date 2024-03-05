package com.example.springskeleton.exception;

public class MemberPersistenceException extends MemberException {
    public MemberPersistenceException(String s) {
        super(s);
    }

    public MemberPersistenceException(String s, Exception e) {
        super(s, e);
    }
}
