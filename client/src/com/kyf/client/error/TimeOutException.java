package com.kyf.client.error;

public class TimeOutException  extends RuntimeException {
    public TimeOutException(String message){
        super(message);
    }
}
