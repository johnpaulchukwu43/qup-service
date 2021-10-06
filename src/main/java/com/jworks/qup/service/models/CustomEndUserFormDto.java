package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import com.jworks.qup.service.enums.CustomEndUserFormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomEndUserFormDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private String description;

    private String formCode;

    private CustomEndUserFormType formType;

    private EndUserQueueInfo associatedQueue;

    private List<CustomEndUserQuestionDto> questions = new ArrayList<>();

    public CustomEndUserFormDto(CustomEndUserForm customEndUserForm) {
        BeanUtils.copyProperties(customEndUserForm, this);

        this.formType = customEndUserForm.getCustomEndUserFormType();

        this.associatedQueue = new EndUserQueueInfo(customEndUserForm.getEndUserQueue());

        List<CustomEndUserQuestion> customEndUserQuestions = customEndUserForm.getCustomEndUserQuestions();

        if (customEndUserQuestions != null && !customEndUserQuestions.isEmpty()) {
            customEndUserQuestions.forEach(question -> this.questions.add(new CustomEndUserQuestionDto(question)));
        }
    }
}
