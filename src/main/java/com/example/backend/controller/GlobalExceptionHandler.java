package com.example.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles uncaught exceptions throughout the application and displays them to the user through an error page
     *
     * @param ex    The caught exception
     * @param model The UI model
     * @return The error page template
     */
    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex, Model model) {
        log.error(ex.getMessage(), ex);
        model.addAttribute("message", ex.getMessage());
        return "error";
    }

}
