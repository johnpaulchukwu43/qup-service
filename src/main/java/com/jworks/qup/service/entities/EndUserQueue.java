package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.QueueLocationType;
import com.jworks.qup.service.enums.QueuePurpose;
import com.jworks.qup.service.enums.QueueStatus;
import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Entity(name="end_user_queues")
@Data
@Builder
@Indexed
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_user_queues",
        indexes = {
        @Index(name = "QUEUE_STATUS_INDEX", columnList = "queue_status"),
        @Index(name = "QUEUE_CODE_INDEX", columnList = "queue_code"),
        @Index(name = "QUEUE_PURPOSE_INDEX", columnList = "queue_purpose"),
        @Index(name = "QUEUE_LOCATION_TYPE_INDEX", columnList = "queue_location_type"),
        @Index(name = "QUEUE_NAME_INDEX", columnList = "name")
})
public class EndUserQueue extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "name", length = 100, nullable = false, unique = true)
    private String name;

    @Column(name = "capacity", nullable = false)
    private Long capacity;

    @Column(name = "expiration_date",nullable = false)
    private LocalDateTime expirationDate;


    @Enumerated(EnumType.STRING)
    @Column(name = "queue_status", nullable = false,length = 100)
    private QueueStatus queueStatus;

    @Column(name = "queue_code",nullable = false, length = 50, unique = true)
    private String queueCode;


    @Enumerated(EnumType.STRING)
    @Column(name = "queue_purpose", nullable = false,length = 50)
    private QueuePurpose queuePurpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "queue_location_type", nullable = false,length = 50)
    private QueueLocationType queueLocationType;

    @Column(nullable = false)
    private String queueLocationValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_owner_id", referencedColumnName = "id", nullable = false)
    private EndUser endUser;

    @OneToOne(fetch = FetchType.EAGER, mappedBy = "queue")
    private EndUserPoolConfig poolConfig;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", referencedColumnName = "id")
    private Business business;
}
