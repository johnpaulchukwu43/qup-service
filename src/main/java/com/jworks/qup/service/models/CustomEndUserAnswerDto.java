package com.jworks.qup.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomEndUserAnswerDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private String answer;

    @NotNull
    private Long questionId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Answers implements Serializable {

        @Valid
        private List<CustomEndUserAnswerDto> userQuestionAnswerList;

        private String userReferenceOfAnswerProvider;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationError implements Serializable {

        private Long questionId;

        private String errorMessage;
    }

}
