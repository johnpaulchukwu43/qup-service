package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
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
public class EndUserVerifyDto implements Serializable {

    @NotBlank(message = "Verification Code is a required field.")
    private String verificationCode;

    @NotBlank(message = "User reference is a required field.")
    private String userReference;

}
