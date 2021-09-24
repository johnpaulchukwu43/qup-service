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

        ValidationError.ValidationErrorBuilder validationErrorBuilder = ValidationError.builder();

        CustomEndUserAnswerType customEndUserAnswerType = question.getCustomEndUserAnswerType();

        String answerValue = answer.getAnswer();

        Long maxAnswerLength = question.getMaxAnswerLength();

        Long minAnswerLength = question.getMinAnswerLength();

        switch (customEndUserAnswerType) {

            case NUMERIC:
                return validateNumericInputs(question, validationErrorBuilder, answerValue, maxAnswerLength, minAnswerLength);

            case EMAIL:
                return validateEmailInput(question, validationErrorBuilder, answerValue);

            case DATE:
                //todo add validation for date values
                return Optional.empty();
            case TIME:
                //todo add validation for time values
                return Optional.empty();

            case ALPHA_NUMERIC:
                return validateAlphaNumericInput(question, validationErrorBuilder, answerValue, maxAnswerLength, minAnswerLength);

            default:
                throw new IllegalArgumentException("Unsupported operation..");
        }
    }

    private Optional<ValidationError> validateEmailInput(CustomEndUserQuestion question, ValidationError.ValidationErrorBuilder validationErrorBuilder, String answerValue) {

        if (!isEmail(answerValue)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage("Invalid format entered for email")
                    .build());
        }

        return Optional.empty();

    }

    private Optional<ValidationError> validateNumericInputs(CustomEndUserQuestion question, ValidationError.ValidationErrorBuilder validationErrorBuilder, String answerValue, Long maxAnswerLength, Long minAnswerLength) {
        if (!IsNumeric(answerValue)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage("Invalid Format. Expecting numbers only.")
                    .build());
        }

        if (!isWithinCharacterLimit(answerValue, true, maxAnswerLength)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage(String.format("Answer provided is more than expected word limit of %s", maxAnswerLength))
                    .build());
        }

        if (!isWithinCharacterLimit(answerValue, false, minAnswerLength)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage(String.format("Answer provided is less than expected length of %s", maxAnswerLength))
                    .build());
        }

        return Optional.empty();
    }

    private Optional<ValidationError> validateAlphaNumericInput(CustomEndUserQuestion question, ValidationError.ValidationErrorBuilder validationErrorBuilder, String answerValue, Long maxAnswerLength, Long minAnswerLength) {

        if (!isWithinCharacterLimit(answerValue, true, maxAnswerLength)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage(String.format("Answer provided is more than expected word limit of %s", maxAnswerLength))
                    .build());
        }

        if (!isWithinCharacterLimit(answerValue, false, minAnswerLength)) {
            return Optional.ofNullable(validationErrorBuilder.
                    questionId(question.getId())
                    .errorMessage(String.format("Answer provided is less than expected length of %s", maxAnswerLength))
                    .build());
        }

        return Optional.empty();
    }


}
