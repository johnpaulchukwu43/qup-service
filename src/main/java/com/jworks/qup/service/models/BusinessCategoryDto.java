package com.jworks.qup.service.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jworks.qup.service.entities.Business;
import com.jworks.qup.service.entities.BusinessCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import javax.validation.constraints.NotBlank;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BusinessCategoryDto implements Serializable {

    @NotBlank(message = "name is required")
    private String name;

    private String description;
}
