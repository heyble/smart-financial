package com.smart.financial.controller.handler;

import com.smart.financial.common.SmartException;
import com.smart.financial.controller.vo.Response;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler(SmartException.class)
    public Response<?> smartExceptionHandler(SmartException ex){
        return new Response<>(500,ex.getMessage());
    }
}
