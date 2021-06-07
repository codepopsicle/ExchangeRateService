package com.scalabale.springboot;

import com.scalabale.springboot.exception.RatesNotInitializedException;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;


@ControllerAdvice
public class ControllerExceptionHandler {

    Logger logger = LoggerFactory.getLogger(ControllerExceptionHandler.class);

    @ResponseStatus(HttpStatus.BAD_REQUEST) // 400
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseBody
    public String handleIllegalArguments(IllegalArgumentException ex){

        logger.error(ex.getMessage());
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(JSONException.class)
    @ResponseBody
    public String handleJSONException(JSONException ex){

        logger.error(ex.getMessage());
        return ex.getMessage();
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) // 500
    @ExceptionHandler(RatesNotInitializedException.class)
    @ResponseBody
    public String handleUninitializedRates(RatesNotInitializedException ex){

        logger.error(ex.getMessage());
        return ex.getMessage();
    }
}
