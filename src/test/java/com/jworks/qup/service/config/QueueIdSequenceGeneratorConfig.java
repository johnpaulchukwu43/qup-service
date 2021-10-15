package com.jworks.qup.service.config;

import com.jworks.qup.service.repositories.QueueIdSequenceRepository;
import com.jworks.qup.service.services.QueueIdSequenceGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author bodmas
 * @since Oct 14, 2021.
 */
@Configuration
public class QueueIdSequenceGeneratorConfig {

    /**
     * We supply a bean here (instead of instantiating with "new") so that we can get the @Transactional methods on
     * QueueIdSequenceGenerator to work.
     * @param queueIdSequenceRepository
     * @return
     */
    @Bean // To avoid bean name conflicts, we use qIdSequenceGenerator instead of queueIdSequenceGenerator.
    public QueueIdSequenceGenerator qIdSequenceGenerator(QueueIdSequenceRepository queueIdSequenceRepository) {
        return new QueueIdSequenceGenerator(queueIdSequenceRepository);
    }
}
