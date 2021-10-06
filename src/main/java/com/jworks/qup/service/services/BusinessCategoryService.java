package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.DuplicateEntryException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.BusinessCategory;
import com.jworks.qup.service.models.BusinessCategoryDto;
import com.jworks.qup.service.repositories.BusinessCategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author bodmas
 * @since Oct 2, 2021.
 */
@Slf4j
@Service
public class BusinessCategoryService extends ServiceBluePrintImpl<BusinessCategory, BusinessCategoryDto> {

    public BusinessCategoryService(BusinessCategoryRepository businessCategoryRepository) {
        super(businessCategoryRepository);
    }

    public BusinessCategoryDto createBusinessCategory(BusinessCategoryDto businessCategoryDto, String userReference)
            throws SystemServiceException {

        ensureCategoryNameIsUnique(businessCategoryDto.getName());
        BusinessCategory businessCategory = buildFromDto(businessCategoryDto);
        save(businessCategory);

        return businessCategoryDto;
    }

    private BusinessCategory buildFromDto(BusinessCategoryDto businessCategoryDto) {
        return BusinessCategory.builder().name(businessCategoryDto.getName())
                .description(businessCategoryDto.getDescription()).build();
    }

    private void ensureCategoryNameIsUnique(String name) throws DuplicateEntryException {
        if (((BusinessCategoryRepository) baseRepository).existsByName(name))
            throw new DuplicateEntryException(String.format("Business category name %s is already taken", name));
    }
}
