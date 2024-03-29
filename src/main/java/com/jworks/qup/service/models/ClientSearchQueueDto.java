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
public class ClientSearchQueueDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String queueStatus;

    private String queueCode;

    private String queuePurpose;

    private String createdOnStartDate;

    private String createdOnEndDate;

    private String userReference;

    private String expiryStartDate;

    private String expiryEndDate;

}
