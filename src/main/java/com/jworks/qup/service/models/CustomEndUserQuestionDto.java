package com.jworks.qup.service.models;

import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.enums.CustomEndUserAnswerType;
import lombok.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Indexed;

import javax.persistence.Entity;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Entity
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CustomEndUserQuestionDto extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String question;

    private CustomEndUserAnswerType customEndUserAnswerType;

    private Long minAnswerLength;

    private Long maxAnswerLength;

    public CustomEndUserQuestionDto(CustomEndUserQuestion entity) {
        BeanUtils.copyProperties(entity, this);
    }
}
