package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.CustomEndUserFormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

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
@Table(name = "custom_end_user_forms", indexes = {
        @Index(name = "FORM_NAME_IDX", columnList = "name"),
        @Index(name = "FORM_CODE_IDX", columnList = "form_code"),
        @Index(name = "FORM_STATUS_INDEX", columnList = "status")
})
public class CustomEndUserForm extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", nullable = false, length = 70)
    private String name;

    @Column
    private String description;

    @Column(name = "form_code", nullable = false, length = 70)
    private String formCode;


    @Column(name = "form_type", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private CustomEndUserFormType customEndUserFormType;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_id", referencedColumnName = "id", nullable = false)
    private EndUserQueue endUserQueue;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "customEndUserForm")
    private List<CustomEndUserQuestion> customEndUserQuestions;
}
