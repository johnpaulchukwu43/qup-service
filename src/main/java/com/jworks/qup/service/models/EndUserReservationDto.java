package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.qup.service.entities.EndUserQueue;
import com.jworks.qup.service.entities.EndUserReservation;
import com.jworks.qup.service.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EndUserReservationDto implements Serializable {

    private Long id;

    private Timestamp createdAt;

    private Timestamp updatedAt;

    private ReservationStatus reservationStatus;

    private String reservationCode;

    private String nameOfUser;

    private EndUserQueue endUserQueue;

    public EndUserReservationDto(EndUserReservation endUserReservation) {
        BeanUtils.copyProperties(endUserReservation, this);
        this.nameOfUser = endUserReservation.getEndUser().getFirstName() + ' '+ endUserReservation.getEndUser().getLastName();
    }
}
