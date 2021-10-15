package com.jworks.qup.service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.io.Serializable;
import lombok.Builder;

/**
 * @author bodmas
 * @since Oct 6, 2021.
 */
@Entity
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "queue_id_sequence", uniqueConstraints = @UniqueConstraint(columnNames = {"queueId", "lastJoinId"}))
public class QueueIdSequence extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * Id of the queue.
     */
    @Column(nullable = false)
    private Long queueId;

    /**
     * Last join id used in queue.
     */
    @Column(nullable = false)
    @Builder.Default
    private Long lastJoinId = 0L;

    @Transient
    private EndUserQueue transientQueue;
}
