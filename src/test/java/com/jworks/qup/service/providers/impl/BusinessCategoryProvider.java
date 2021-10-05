package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.BusinessCategory;
import com.jworks.qup.service.models.BusinessCategoryDto;
import com.jworks.qup.service.providers.DtoProvider;
import com.jworks.qup.service.repositories.BusinessCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;
import static com.jworks.qup.service.providers.EntityProvider.faker;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
@Component
@AllArgsConstructor
public class BusinessCategoryProvider implements EntityProvider<BusinessCategory>, DtoProvider<BusinessCategoryDto> {

    private final BusinessCategoryRepository businessCategoryRepository;

    @Override
    public BusinessCategory provide() {
        BusinessCategory businessCategory = BusinessCategory.builder()
                .description(faker.company().catchPhrase()).name(faker.company().name()).build();
        return businessCategory;
    }

    @Override
    public BusinessCategoryDto provideDto() {
        BusinessCategoryDto businessCategoryDto = BusinessCategoryDto.builder()
                .description(faker.company().catchPhrase()).name(faker.company().name()).build();
        return businessCategoryDto;
    }

    @Override
    public BusinessCategoryRepository getRepository() {
        return businessCategoryRepository;
    }
}
