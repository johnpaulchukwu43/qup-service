package com.jworks.qup.service.models;

import com.jworks.app.commons.validator.AcceptedPasswordFormat;
import com.jworks.app.commons.validator.ConditionalInputSanitizer;
import com.jworks.app.commons.validator.ValidEnum;
import com.jworks.qup.service.enums.LoginType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * @author Johnpaul Chukwu.
 * @since 15/04/2021
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EndUserLoginDto implements Serializable {

    @ConditionalInputSanitizer(isNullable = true, min = 3)
    private String emailAddress;

    @AcceptedPasswordFormat
    @NotBlank(message = "password is a required field")
    private String password;

    @ConditionalInputSanitizer(isNullable = true, min = 10)
    private String phoneNumber;

    @ValidEnum(enumClass = LoginType.class)
    private String loginType;
}
