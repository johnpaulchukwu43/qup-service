package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class EndUserDto implements Serializable {

    private String firstName;

    private String lastName;

    private String emailAddress;

    private String username;

    private String phoneNumber;

    private String userReference;


}
