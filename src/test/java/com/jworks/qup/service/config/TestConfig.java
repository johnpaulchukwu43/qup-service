package com.jworks.qup.service.config;

import com.jworks.qup.service.providers.impl.EndUserProvider;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author bodmas
 * @since Oct 4, 2021.
 */
@Configuration
@ComponentScan(basePackageClasses = {EndUserProvider.class})
public class TestConfig {
}
