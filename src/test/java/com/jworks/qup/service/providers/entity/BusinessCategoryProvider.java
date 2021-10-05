package com.jworks.qup.service.providers.entity;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.BusinessCategory;
import com.jworks.qup.service.repositories.BusinessCategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
@Component
@AllArgsConstructor
public class BusinessCategoryProvider implements EntityProvider<BusinessCategory> {

    private final BusinessCategoryRepository businessCategoryRepository;

    @Override
    public BusinessCategory provide() {
        BusinessCategory businessCategory = BusinessCategory.builder()
                .description(faker.company().catchPhrase()).name(faker.company().name()).build();
        return businessCategory;
    }

    @Override
    public BusinessCategoryRepository getRepository() {
        return businessCategoryRepository;
    }
}
