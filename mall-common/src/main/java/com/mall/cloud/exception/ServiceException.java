package com.mall.cloud.exception;

import com.mall.cloud.common.StatusCode;
import lombok.Data;

@Data
public class ServiceException  extends RuntimeException {

    private int status;

    public ServiceException(String message) {
        super(message);
        this.status = StatusCode.ERROR;
    }

    public ServiceException(Exception e) {
        super(e.getMessage());
        this.status = StatusCode.ERROR;

    }
}