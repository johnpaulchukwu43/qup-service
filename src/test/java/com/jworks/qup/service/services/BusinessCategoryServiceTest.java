/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jworks.qup.service.services;

import com.jworks.qup.service.entities.BusinessCategory;
import com.jworks.qup.service.providers.entity.BusinessCategoryProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Tests for {@link BusinessCategoryService}.
 *
 * @author bodmas
 * @since Oct 4, 2021.
 */
public class BusinessCategoryServiceTest extends AbstractServiceTest {

    @Autowired
    private BusinessCategoryProvider businessCategoryProvider;

    @BeforeEach
    public void setUp() {
    }

    @Test
    public void testCreateBusinessCategory() throws Exception {
        BusinessCategory businessCategory = businessCategoryProvider.provide();
        businessCategoryProvider.save(businessCategory);
        Assertions.assertTrue(businessCategoryProvider.getRepository().existsByName(businessCategory.getName()));
    }
}
