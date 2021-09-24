package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.enums.EntityStatus;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.enums.CustomEndUserAnswerType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CreateCustomEndUserQuestionDto extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotBlank
    private String question;

    @ValidEnum(enumClass = CustomEndUserAnswerType.class)
    private String answerType;

    @NotNull
    @PositiveOrZero
    private Long minAnswerLength;

    @NotNull
    @PositiveOrZero
    private Long maxAnswerLength;

    @ValidEnum(enumClass = EntityStatus.class)
    private String status;
}
