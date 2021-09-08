package com.jworks.qup.service.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 11/08/2021
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToggleCustomFormRequirementDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @NotNull
    private Long queueId;

    private boolean requiresQueueForm;

    private Long customFormId;

}
