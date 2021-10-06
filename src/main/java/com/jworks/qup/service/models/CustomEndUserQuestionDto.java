package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.enums.CustomEndUserAnswerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomEndUserQuestionDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String question;

    private CustomEndUserAnswerType customEndUserAnswerType;

    private Long minAnswerLength;

    private Long maxAnswerLength;

    private boolean isRequired;

    public CustomEndUserQuestionDto(CustomEndUserQuestion entity) {
        BeanUtils.copyProperties(entity, this);
    }
}
