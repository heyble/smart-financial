package com.smart.financial.controller.handler;

import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(SmartException.class)
    public Response<?> smartExceptionHandler(SmartException ex){
        LOGGER.error("业务异常",ex);
        return new Response<>(500,ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public Response<?> exceptionHandler(Exception ex){
        LOGGER.error("系统异常",ex);
        return new Response<>(500,ex.getMessage());
    }
}
