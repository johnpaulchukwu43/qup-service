package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 23/09/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ClientSearchQueueInfo implements Serializable {
    private static final long serialVersionUID = 1L;

    private String queueName;

    private String queueCode;

    private String businessName;

}
