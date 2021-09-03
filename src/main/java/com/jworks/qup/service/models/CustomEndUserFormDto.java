package com.jworks.qup.service.models;

import com.jworks.qup.service.entities.BaseEntity;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.enums.CustomEndUserFormType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Indexed;

import javax.persistence.Entity;
import java.io.Serializable;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
public class CustomEndUserFormDto extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;

    private String description;

    private String formCode;

    private CustomEndUserFormType formType;

    private EndUserQueueInfo associatedQueue;

    private List<CustomEndUserQuestionDto> questions;

    public CustomEndUserFormDto(CustomEndUserForm customEndUserForm) {
        BeanUtils.copyProperties(customEndUserForm, this);

        this.formType = customEndUserForm.getCustomEndUserFormType();

        this.associatedQueue = new EndUserQueueInfo(customEndUserForm.getEndUserQueue());

        customEndUserForm.getCustomEndUserQuestions().forEach(question -> this.questions.add(new CustomEndUserQuestionDto(question)));

    }
}
