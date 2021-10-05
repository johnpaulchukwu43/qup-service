package com.jworks.qup.service.providers.dto;

import com.jworks.qup.service.models.BusinessCategoryDto;
import com.jworks.qup.service.providers.Provider;
import org.springframework.stereotype.Component;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
@Component
public class BusinessCategoryDtoProvider implements Provider<BusinessCategoryDto> {

    @Override
    public BusinessCategoryDto provide() {
        BusinessCategoryDto businessCategoryDto = BusinessCategoryDto.builder()
                .description(faker.company().catchPhrase()).name(faker.company().name()).build();
        return businessCategoryDto;
    }
}
