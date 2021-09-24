package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.app.commons.repositories.projection.IdProjection;
import com.jworks.qup.service.entities.EndUserOnboardRequest;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author Johnpaul Chukwu.
 * @since 17/12/2020
 */

@Repository
public interface EndUserOnboardRequestRepository extends BaseRepository<EndUserOnboardRequest,Long> {

    Optional<IdProjection> findIdByEmailAddress(String emailAddress);

    Optional<IdProjection> findIdByPhoneNumber(String phoneNumber);

    Optional<EndUserOnboardRequest> findByVerificationCodeAndUserReference(String verificationCode, String userReference);

}
