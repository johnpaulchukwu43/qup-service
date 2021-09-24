package com.jworks.qup.service.entities;

import com.jworks.qup.service.enums.ReservationStatus;
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
@Table(name = "end_user_reservations",
        indexes = {
        @Index(name = "RESERVATION_STATUS_INDEX", columnList = "reservation_status"),
        @Index(name = "RESERVATION_CODE_INDEX", columnList = "reservation_code"),
})
public class EndUserReservation extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false,length = 100)
    private ReservationStatus reservationStatus;

    @Column(name = "reservation_code",nullable = false, length = 50, unique = true)
    private String reservationCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "end_user_id", referencedColumnName = "id" , nullable = false)
    private EndUser endUser;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "queue_id", referencedColumnName = "id", nullable = false)
    private EndUserQueue endUserQueue;
}
