package com.jworks.qup.service.entities;

import lombok.*;
import org.springframework.stereotype.Indexed;

import javax.persistence.*;
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
@EqualsAndHashCode(callSuper = true)
@Table(name = "end_users",
        indexes = {
        @Index(name = "STATUS_INDEX", columnList = "status"),
        @Index(name = "EMAIL_ADDRESS_INDEX", columnList = "email_address"),
        @Index(name = "USER_REFERENCE_INDEX", columnList = "user_reference"),
        @Index(name = "PHONE_NUMBER_INDEX", columnList = "phone_number")
})
public class EndUser extends BaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "user_reference", nullable = false, length = 63, unique = true, updatable = false)
    private String userReference;

    @Column(name = "email_address", length = 60, unique = true)
    private String emailAddress;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "phone_number", length = 20, unique = true)
    private String phoneNumber;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserQueue> endUserQueues;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<Business> businesses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserReservation> endUserReservations;
}
