package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.app.commons.validator.AcceptedPasswordFormat;
import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.EndUserOnBoardVerificationOption;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
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
public class EndUserOnboardRequestDto implements Serializable {

    @Size(min = 2, message = "first name must be at least 2 characters long.")
    private String firstName;

    @Size(min = 2, message = "last name must be at least 2 characters long.")
    private String lastName;

    @ConditionalInputSanitizer(isNullable = true, min = 3)
    private String emailAddress;

    @AcceptedPasswordFormat
    @NotBlank(message = "password is a required field")
    private String password;

    @ConditionalInputSanitizer(isNullable = true, min = 10)
    private String phoneNumber;

    @ValidEnum(enumClass = EndUserOnBoardVerificationOption.class)
    private String endUserOnBoardVerificationOption;
}
