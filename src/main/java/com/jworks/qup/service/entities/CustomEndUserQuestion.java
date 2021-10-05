package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.CustomEndUserAnswerType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "custom_end_user_questions", indexes = {
        @Index(name = "QUESTION_STATUS_INDEX", columnList = "status")
})
public class CustomEndUserQuestion extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @Column(name = "answer_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CustomEndUserAnswerType customEndUserAnswerType;

    private Long minAnswerLength;

    private Long maxAnswerLength;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "form_id", referencedColumnName = "id", nullable = false)
    private CustomEndUserForm customEndUserForm;


    private boolean isRequired;
}
