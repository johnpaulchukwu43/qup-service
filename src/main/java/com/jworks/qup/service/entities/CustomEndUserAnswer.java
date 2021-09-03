package com.jworks.qup.service.entities;

import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;

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
@EqualsAndHashCode(callSuper = true)
@Table(name = "custom_end_user_answers")
public class CustomEndUserAnswer extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, columnDefinition = "LONGTEXT")
    private String answer;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "answered_by_user_id", referencedColumnName = "id", nullable = false)
    private EndUser endUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", referencedColumnName = "id", nullable = false)
    private CustomEndUserQuestion customEndUserQuestion;
}
