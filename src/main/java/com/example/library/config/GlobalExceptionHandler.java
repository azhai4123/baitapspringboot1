package com.example.library.config;

import com.example.library.exception.BusinessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException exception) {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(exception instanceof com.example.library.exception.ResourceNotFoundException
                ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST);
        view.addObject("message", exception.getMessage());
        return view;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ModelAndView handleDataIntegrityViolation() {
        ModelAndView view = new ModelAndView("error");
        view.setStatus(HttpStatus.CONFLICT);
        view.addObject("message", "Dữ liệu bị trùng hoặc đang được sử dụng. Vui lòng kiểm tra lại.");
        return view;
    }
}
