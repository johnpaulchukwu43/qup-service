package com.jworks.qup.service.providers.impl;

import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.repositories.EndUserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import com.jworks.qup.service.providers.EntityProvider;

/**
 * @author bodmas
 * @since Oct 5, 2021.
 */
@Component
@AllArgsConstructor
public class EndUserProvider implements EntityProvider<EndUser> {

    private final EndUserRepository endUserRepository;

    @Override
    public EndUser provide() {
        EndUser endUser = EndUser.builder()
                .emailAddress(faker.internet().emailAddress())
                .firstName(faker.name().firstName())
                .lastName(faker.name().lastName())
                .password(faker.funnyName().name())
                .phoneNumber(faker.phoneNumber().subscriberNumber(11))
                .userReference(faker.code().ean8())
                .build();
        return endUser;
    }

    @Override
    public EndUserRepository getRepository() {
        return endUserRepository;
    }
}
