package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.CustomEndUserFormType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class UpdateCustomEndUserFormDto implements Serializable {

    private static final long serialVersionUID = 1L;

    @ConditionalInputSanitizer(min = 2, max = 70, message = "form name must be between 2-70 characters long.")
    private String name;

    private String description;

    @ValidEnum(enumClass = CustomEndUserFormType.class)
    private String formType;
}
