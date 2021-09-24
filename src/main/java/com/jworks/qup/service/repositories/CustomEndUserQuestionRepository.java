package com.jworks.qup.service.repositories;

import com.jworks.app.commons.repositories.BaseRepository;
import com.jworks.qup.service.entities.CustomEndUserQuestion;
import org.springframework.stereotype.Repository;


/**
 * @author Johnpaul Chukwu.
 * @since 1/09/2021
 */

@Repository
public interface CustomEndUserQuestionRepository extends BaseRepository<CustomEndUserQuestion, Long> {
}
