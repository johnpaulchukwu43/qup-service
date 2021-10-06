package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomEndUserAnswerEncloser implements Serializable {

    @Valid
    private List<CustomEndUserAnswerDto> userQuestionAnswerList;

    private String userReferenceOfAnswerProvider;
}
