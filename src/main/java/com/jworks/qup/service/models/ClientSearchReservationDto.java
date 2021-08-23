package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 27/04/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientSearchReservationDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String reservationCode;

    private String queueCode;

    private String createdOn;

    private String userReference;

    private String expiryDate;

    private String reservationStatus;

}
