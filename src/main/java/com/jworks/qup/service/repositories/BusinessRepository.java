package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.Business;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface BusinessRepository extends BaseRepository<Business> {

    Optional<Business> findByName(String name);
    Optional<Business> findByEmailAddress(String email);
    Optional<Business> findByPhoneNumber(String phoneNumber);
}
