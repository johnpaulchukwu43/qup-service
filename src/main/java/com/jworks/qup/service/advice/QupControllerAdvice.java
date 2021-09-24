package com.jworks.qup.service.advice;

import com.jworks.app.commons.advice.GeneralControllerAdvice;
import com.jworks.app.commons.models.ApiResponseDto;
import com.jworks.qup.service.exceptions.CustomEndUserFormException;
import com.jworks.qup.service.models.CustomEndUserAnswerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class QupControllerAdvice extends GeneralControllerAdvice {

    @ExceptionHandler(CustomEndUserFormException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponseDto handleCustomEndUserFormException(CustomEndUserFormException ex) {
        log.error(ex.getMessage(), ex);

        Map<String, List<CustomEndUserAnswerDto.ValidationError>> responseMap = new HashMap<>();

        responseMap.put("errorList", ex.getValidationErrors());

        return new ApiResponseDto(ApiResponseDto.Status.fail, ex.getMessage(), (Serializable) responseMap);
    }
}
