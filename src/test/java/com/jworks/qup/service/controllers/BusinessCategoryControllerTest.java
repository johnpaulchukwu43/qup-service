package com.jworks.qup.service.controllers;

import com.jworks.qup.service.models.BusinessCategoryDto;
import com.jworks.qup.service.providers.impl.BusinessCategoryProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link BusinessCategoryController}.
 *
 * @author bodmas
 * @since Oct 4, 2021.
 */
public class BusinessCategoryControllerTest extends AbstractResourceTest {

    @Autowired
    private BusinessCategoryProvider businessCategoryProvider;

    public BusinessCategoryControllerTest() {
        super(BusinessCategoryController.class);
    }

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void createBusinessCategoryShouldSucceed() throws Exception {
        BusinessCategoryDto dto = businessCategoryProvider.provideDto();
        asAdmin(post(dto, endpoint()))
                .andDo(print())
                .andExpect(successStatus());
    }
}
