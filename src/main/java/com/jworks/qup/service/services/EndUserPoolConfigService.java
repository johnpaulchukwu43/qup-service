package com.jworks.qup.service.services;

import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.EndUserPoolConfig;
import com.jworks.qup.service.repositories.EndUserPoolConfigRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
public class EndUserPoolConfigService extends ServiceBluePrintImpl<EndUserPoolConfig, EndUserPoolConfig> {

    private final EndUserPoolConfigRepository endUserPoolConfigRepository;

    public EndUserPoolConfigService(EndUserPoolConfigRepository endUserPoolConfigRepository) {
        super(endUserPoolConfigRepository);
        this.endUserPoolConfigRepository = endUserPoolConfigRepository;
    }
}
