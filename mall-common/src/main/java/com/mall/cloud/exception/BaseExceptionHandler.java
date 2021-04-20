package com.mall.cloud.exception;

import com.mall.cloud.common.Result;
import com.mall.cloud.common.StatusCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class BaseExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result error(Exception e) {
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }


    /**
     * 拦截ServiceException
     */
    @ResponseBody
    @ExceptionHandler(ServiceException.class)
    public Result<ExceptionResult> handlerServiceException(ServiceException e){
        log.error("ERROR", "Error found: ", e);
        return new Result(false, StatusCode.ERROR, e.getMessage());
    }
}
