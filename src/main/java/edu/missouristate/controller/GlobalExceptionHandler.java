package edu.missouristate.controller;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // This redirects the user back to the landing page after declining authorization
    @ExceptionHandler({Exception.class, NoHandlerFoundException.class})
    public String handleException(Exception e) {
        System.err.println("Error occurred: " + e.getMessage());
        return "redirect:/landing";
    }
}

