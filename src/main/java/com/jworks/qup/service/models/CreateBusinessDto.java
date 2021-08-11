package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CreateBusinessDto implements Serializable {

    @ConditionalInputSanitizer(min = 2, max= 70, message = "queueLocationValue must be between 2-70 characters long.")
    private String name;

    private String description;

    private String webSiteUrl;

    private String emailAddress;

    private String phoneNumber;

    private String storeFrontImageUrl;

    private String logoImageUrl;

    @NotNull
    private Long businessCategoryId;
}
