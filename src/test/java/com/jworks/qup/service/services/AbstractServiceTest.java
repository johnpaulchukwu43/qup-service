package com.jworks.qup.service.services;

import com.jworks.qup.service.config.TestConfig;
import com.jworks.qup.service.framework.TestSupport;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

/**
 * @author bodmas
 * @since Oct 4, 2021.
 */
@DataJpaTest
@AutoConfigureTestDatabase
@Import(TestConfig.class)
public class AbstractServiceTest extends TestSupport {
}
