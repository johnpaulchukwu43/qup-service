package com.jworks.qup.service.services;

import com.jworks.app.commons.exceptions.DuplicateEntryException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
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
            throws UnProcessableOperationException, SystemServiceException, NotFoundRestApiException {

        ensureCategoryNameIsUnique(businessCategoryDto.getName());
        BusinessCategory businessCategory = buildFromDto(businessCategoryDto);
        save(businessCategory);

        return businessCategoryDto;
    }

    private BusinessCategory buildFromDto(BusinessCategoryDto businessCategoryDto) {
        BusinessCategory businessCategory = BusinessCategory.builder().name(businessCategoryDto.getName())
                .description(businessCategoryDto.getDescription()).build();
        return businessCategory;
    }

    private void ensureCategoryNameIsUnique(String name) throws DuplicateEntryException {
        if (BusinessCategoryRepository.class.cast(baseRepository).existsByName(name))
            throw new DuplicateEntryException(String.format("Business category name %s is already taken", name));
    }
}
