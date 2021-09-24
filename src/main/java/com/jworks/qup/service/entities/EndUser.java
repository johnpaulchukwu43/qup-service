package com.jworks.qup.service.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
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
@Table(name = "end_users")
public class EndUser extends BaseEndUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserQueue> endUserQueues;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<Business> businesses;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "endUser")
    private List<EndUserReservation> endUserReservations;
}
