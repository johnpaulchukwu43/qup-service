package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.CustomEndUserForm;
import com.jworks.qup.service.models.CustomEndUserFormDto;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


/**
 * @author Johnpaul Chukwu.
 * @since 1/09/2021
 */

@Repository
public interface CustomEndUserFormRepository extends BaseRepository<CustomEndUserForm> {


    @Query("SELECT new com.jworks.qup.service.models.CustomEndUserFormDto(userForm) FROM CustomEndUserForm userForm WHERE userForm.endUserQueue.id = :queueId")
    List<CustomEndUserFormDto> getFormDtoByQueueId(@Param("queueId") Long queueId);

    @Query("SELECT userForm FROM CustomEndUserForm userForm WHERE userForm.endUserQueue.id = :queueId")
    List<CustomEndUserForm> getFormByQueueId(@Param("queueId") Long queueId);

    Optional<CustomEndUserForm> findByFormCode(String formCode);
}
