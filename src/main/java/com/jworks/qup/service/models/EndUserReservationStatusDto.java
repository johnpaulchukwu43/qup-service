package com.jworks.qup.service.models;

import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndUserReservationStatusDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ValidEnum(enumClass = ReservationStatus.class)
    private String reservationStatus;

}
