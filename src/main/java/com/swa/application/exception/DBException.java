package com.swa.application.exception;

public class DBException extends Exception{
    public DBException(String msg) {
        super(msg);
    }

    public DBException() {
        this("A DB exception has occurred");
    }
}
