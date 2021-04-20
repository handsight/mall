package com.mall.cloud.exception;


public class ExceptionResult {
    private int status;
    private String message;

    public ExceptionResult(ServiceException e) {
        this.status = e.getStatus();
        this.message = e.getMessage();
    }
}
