package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.app.commons.repositories.projection.IdProjection;
import com.jworks.qup.service.entities.EndUser;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserRepository extends BaseRepository<EndUser> {

    boolean existsByUserReference(String userReference);
    Optional<IdProjection> findIdByUserReference(String userReference);
    Optional<IdProjection> findIdByPhoneNumber(String phoneNumber);
    Optional<IdProjection> findIdByPhoneNumberAndIdNot(String phoneNumber, Long id);
    Optional<IdProjection> findIdByEmailAddress(String emailAddress);
    Optional<IdProjection> findIdByEmailAddressAndIdNot(String emailAddress, Long id);
    Optional<EndUser> findByUserReference(String userReference);
    Optional<EndUser> findByPhoneNumber(String phoneNumber);
    Optional<EndUser> findByEmailAddress(String emailAddress);
}
