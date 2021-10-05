package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.Business;
import com.jworks.qup.service.repositories.BusinessRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Component
@AllArgsConstructor
public class BusinessProvider implements EntityProvider<Business> {

    private final BusinessRepository businessRepository;
    private final EndUserProvider endUserProvider;
    private final BusinessCategoryProvider businessCategoryProvider;

    @Override
    public Business provide() {
        Business business = Business.builder()
                .webSiteUrl(faker.internet().url())
                .storeFrontImageUrl(faker.internet().url())
                .phoneNumber(faker.phoneNumber().subscriberNumber(11))
                .name(faker.funnyName().name())
                .logoImageUrl(faker.internet().url())
                .endUser(endUserProvider.provide())
                .emailAddress(faker.internet().emailAddress())
                .description(faker.company().name())
                .businessCategory(businessCategoryProvider.provide())
                .build();
        return business;
    }

    @Override
    public void preSave(Business business) {
        if (business.getEndUser() != null)
            endUserProvider.save(business.getEndUser());
        if (business.getBusinessCategory() != null)
            businessCategoryProvider.save(business.getBusinessCategory());
    }

    @Override
    public BusinessRepository getRepository() {
        return businessRepository;
    }
}
