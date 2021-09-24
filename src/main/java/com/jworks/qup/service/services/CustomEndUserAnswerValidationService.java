package com.jworks.qup.service.services;

import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.enums.CustomEndUserAnswerType;
import com.jworks.qup.service.models.CustomEndUserAnswerDto;
import com.jworks.qup.service.models.CustomEndUserAnswerDto.ValidationError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.jworks.app.commons.utils.AppUtil.*;


/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomEndUserAnswerValidationService {


    public Optional<ValidationError> checkAnswerIsValidForQuestion(CustomEndUserAnswerDto answer, CustomEndUserQuestion question) {

        ValidationError validationError = new ValidationError();

        CustomEndUserAnswerType customEndUserAnswerType = question.getCustomEndUserAnswerType();

        String answerValue = answer.getAnswer();

        Long maxAnswerLength = question.getMaxAnswerLength();

        Long minAnswerLength = question.getMinAnswerLength();

        switch (customEndUserAnswerType) {

            case NUMERIC:
                return validateNumericInputs(question, answerValue, maxAnswerLength, minAnswerLength);

            case EMAIL:
                return validateEmailInput(question, answerValue);

            case DATE:
                //todo add validation for date values
                return Optional.empty();
            case TIME:
                //todo add validation for time values
                return Optional.empty();

            case ALPHA_NUMERIC:
                return validateAlphaNumericInput(question, answerValue, maxAnswerLength, minAnswerLength);

            default:
                throw new IllegalArgumentException("Unsupported operation..");
        }
    }

    private Optional<ValidationError> validateEmailInput(CustomEndUserQuestion question, String answerValue) {

        if (!isEmail(answerValue)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage("Invalid format entered for email");
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);
        }

        return Optional.empty();

    }

    private Optional<ValidationError> validateNumericInputs(CustomEndUserQuestion question, String answerValue, Long maxAnswerLength, Long minAnswerLength) {
        if (!IsNumeric(answerValue)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage("Invalid Format. Expecting numbers only.");
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);
        }

        if (!isWithinCharacterLimit(answerValue, true, maxAnswerLength)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage(String.format("Answer provided is more than expected word limit of %s", maxAnswerLength));
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);
        }

        if (!isWithinCharacterLimit(answerValue, false, minAnswerLength)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage(String.format("Answer provided is less than expected length of %s", maxAnswerLength));
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);

        }

        return Optional.empty();
    }

    private Optional<ValidationError> validateAlphaNumericInput(CustomEndUserQuestion question, String answerValue, Long maxAnswerLength, Long minAnswerLength) {

        if (!isWithinCharacterLimit(answerValue, true, maxAnswerLength)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage(String.format("Answer provided is more than expected word limit of %s", maxAnswerLength));
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);

        }

        if (!isWithinCharacterLimit(answerValue, false, minAnswerLength)) {
            ValidationError validationError = new ValidationError();
            validationError.setErrorMessage(String.format("Answer provided is less than expected length of %s", maxAnswerLength));
            validationError.setQuestionId(question.getId());
            return Optional.ofNullable(validationError);
        }

        return Optional.empty();
    }


}
