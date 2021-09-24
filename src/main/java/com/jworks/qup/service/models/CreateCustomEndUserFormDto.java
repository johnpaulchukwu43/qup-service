package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import javax.validation.Valid;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 01/09/2021
 */

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class CreateCustomEndUserFormDto extends UpdateCustomEndUserFormDto {

    @Valid
    private List<CreateCustomEndUserQuestionDto> questions;
}
