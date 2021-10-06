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

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class BusinessDto implements Serializable {

    private Long id;

    private String name;

    private String description;

    private String webSiteUrl;

    private String emailAddress;

    private String phoneNumber;

    private String storeFrontImageUrl;

    private String logoImageUrl;

    private BusinessCategory businessCategory;

    public BusinessDto (Business business){
        BeanUtils.copyProperties(business,this);
    }
}
