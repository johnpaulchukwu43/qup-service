package com.jworks.qup.service.entities;

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

@Data
@Entity
@SuperBuilder
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
public class EndUser extends BaseEndUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserQueue> endUserQueues;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<Business> businesses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserReservation> endUserReservations;
}
