package com.jworks.qup.service.services;

import com.jworks.app.commons.enums.EntityStatus;
import com.jworks.app.commons.exceptions.DuplicateEntryException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.Business;
import com.jworks.qup.service.entities.BusinessCategory;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.models.BusinessDto;
import com.jworks.qup.service.models.CreateBusinessDto;
import com.jworks.qup.service.repositories.BusinessCategoryRepository;
import com.jworks.qup.service.repositories.BusinessRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;


/**
 * @author Johnpaul Chukwu.
 * @since 30/07/2021
 */

@Slf4j
@Service
public class BusinessService extends ServiceBluePrintImpl<Business, BusinessDto> {

    private final BusinessRepository businessRepository;
    private final BusinessCategoryRepository businessCategoryRepository;
    private final EndUserService endUserService;

    public BusinessService(BusinessRepository businessRepository, BusinessCategoryRepository businessCategoryRepository, EndUserService endUserService) {
        super(businessRepository);
        this.businessRepository = businessRepository;
        this.businessCategoryRepository = businessCategoryRepository;
        this.endUserService = endUserService;
    }


    public BusinessDto createBusiness(CreateBusinessDto createBusinessDto, String userReference) throws UnProcessableOperationException, SystemServiceException, NotFoundRestApiException {

        Long businessCategoryId = createBusinessDto.getBusinessCategoryId();

        EndUser ownerOfQueue = endUserService.getUserByUserReference(userReference);

        BusinessCategory businessCategory = businessCategoryRepository.findById(businessCategoryId)
                .orElseThrow(() -> new UnProcessableOperationException(
                        String.format("Business category with id: %s does not exist.", businessCategoryId)));

        ensureNameDoesNotExist(createBusinessDto.getName());
        ensureEmailDoesNotExist(createBusinessDto.getEmailAddress());
        ensurePhoneNumberDoesNotExist(createBusinessDto.getPhoneNumber());

        Business business = new Business();
        BeanUtils.copyProperties(createBusinessDto, business);
        business.setBusinessCategory(businessCategory);
        business.setEndUser(ownerOfQueue);
        business.setEntityStatus(EntityStatus.ACTIVE);

        return new BusinessDto(save(business));
    }

    private void ensureNameDoesNotExist(String name) throws DuplicateEntryException {
        if (businessRepository.findByName(name).isPresent()) {
            throw new DuplicateEntryException(String.format("Business name: %s is already taken.", name));
        }
    }

    private void ensureEmailDoesNotExist(String email) throws DuplicateEntryException {
        if (!StringUtils.isEmpty(email) && businessRepository.findByEmailAddress(email).isPresent()) {
            throw new DuplicateEntryException(String.format("Email address: %s is already taken.", email));
        }
    }

    private void ensurePhoneNumberDoesNotExist(String phoneNumber) throws DuplicateEntryException {
        if (!StringUtils.isEmpty(phoneNumber) && businessRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateEntryException(String.format("PhoneNumber: %s is already taken.", phoneNumber));
        }
    }
}
