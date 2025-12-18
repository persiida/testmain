package ru.rutmiit.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.rutmiit.models.exceptions.CompanyNotFoundException;
import ru.rutmiit.models.exceptions.EmployeeNotFoundException;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompanyNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleCompanyNotFound(CompanyNotFoundException ex, Model model) {
        log.warn("Компания не найдена: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Компания не найдена");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/custom-error";
    }

    @ExceptionHandler(EmployeeNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleEmployeeNotFound(EmployeeNotFoundException ex, Model model) {
        log.warn("Сотрудник не найден: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Сотрудник не найден");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "404");
        return "error/custom-error";
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        log.warn("Некорректные данные: {}", ex.getMessage());
        model.addAttribute("errorTitle", "Некорректные данные");
        model.addAttribute("errorMessage", ex.getMessage());
        model.addAttribute("errorCode", "400");
        return "error/custom-error";
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGenericException(Exception ex, Model model) {
        log.error("Внутренняя ошибка сервера", ex);
        model.addAttribute("errorTitle", "Внутренняя ошибка сервера");
        model.addAttribute("errorMessage", "Произошла непредвиденная ошибка. Пожалуйста, попробуйте позже.");
        model.addAttribute("errorCode", "500");
        return "error/custom-error";
    }
}
