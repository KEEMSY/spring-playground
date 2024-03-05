package com.example.springskeleton.exception;

public class MemberServiceException extends MemberException {
    public MemberServiceException(String s) {
        super(s);
    }

    public MemberServiceException(String s, Exception e) {
        super(s, e);
    }
}
