package com.jworks.qup.service.exceptions;

import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.qup.service.models.CustomEndUserAnswerDto;
import lombok.Getter;

import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 24/12/2020
 */

@Getter
public class CustomEndUserFormException extends BadRequestException {

    private List<CustomEndUserAnswerDto.ValidationError> validationErrors;

    public CustomEndUserFormException() {
    }

    public CustomEndUserFormException(String message, Throwable inner) {
        super(message, inner);
    }

    public CustomEndUserFormException(Throwable inner) {
        super(null, inner);
    }

    public CustomEndUserFormException(String message) {
        super(message);
    }

    public CustomEndUserFormException(String message, Object[] args) {
        super(message, args);
    }

    public CustomEndUserFormException(String message, List<CustomEndUserAnswerDto.ValidationError> validationErrors) {
        super(message);
        this.validationErrors = validationErrors;

    }


}
