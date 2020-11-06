package com.virtualmind.jrfexams;

public class Payment {
    private boolean error;
    private String status;
    private String message;
    private String cftoken;

    public boolean isError() {
        return error;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getCftoken() {
        return cftoken;
    }
}
