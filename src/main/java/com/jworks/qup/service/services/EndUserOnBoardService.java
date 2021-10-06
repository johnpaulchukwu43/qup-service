package com.jworks.qup.service.services;

import com.jworks.app.commons.enums.EntityStatus;
import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.DuplicateEntryException;
import com.jworks.app.commons.exceptions.SystemServiceException;
import com.jworks.app.commons.exceptions.UnProcessableOperationException;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.app.commons.utils.ReferenceGenerator;
import com.jworks.qup.service.entities.EndUserOnboardRequest;
import com.jworks.qup.service.enums.EndUserOnBoardVerificationOption;
import com.jworks.qup.service.models.EndUserNotificationTemplateCode;
import com.jworks.qup.service.models.EndUserOnboardRequestDto;
import com.jworks.qup.service.models.EndUserVerifyDto;
import com.jworks.qup.service.repositories.EndUserOnboardRequestRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_USER_REFERENCE;
import static com.jworks.app.commons.utils.ReferenceGenerator.INTENT_VERIFICATION_CODE;
import static com.jworks.qup.service.enums.EndUserOnBoardVerificationOption.EMAIL;
import static com.jworks.qup.service.enums.EndUserOnBoardVerificationOption.PHONE_NUMBER;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */
@Slf4j
@Service
public class EndUserOnBoardService extends ServiceBluePrintImpl<EndUserOnboardRequest, EndUserOnboardRequest> {


    private final EndUserOnboardRequestRepository endUserOnboardRequestRepository;

    private final PasswordEncoder passwordEncoder;

    private final EndUserService endUserService;

    private final EventNotifyService eventNotifyService;

    @Autowired
    public EndUserOnBoardService(EndUserOnboardRequestRepository endUserOnboardRequestRepository, PasswordEncoder passwordEncoder, EndUserService endUserService, EventNotifyService eventNotifyService) {
        super(endUserOnboardRequestRepository);
        this.endUserOnboardRequestRepository = endUserOnboardRequestRepository;
        this.passwordEncoder = passwordEncoder;
        this.endUserService = endUserService;
        this.eventNotifyService = eventNotifyService;
    }


    @Transactional
    public String performUserOnboardProcess(EndUserOnboardRequestDto endUserOnboardRequestDto) throws SystemServiceException {


        EndUserOnBoardVerificationOption endUserOnBoardVerificationOption = EndUserOnBoardVerificationOption.valueOf(endUserOnboardRequestDto.getEndUserOnBoardVerificationOption());
        String phoneNumber = endUserOnboardRequestDto.getPhoneNumber();
        String emailAddress = endUserOnboardRequestDto.getEmailAddress();

        if (PHONE_NUMBER.equals(endUserOnBoardVerificationOption) && StringUtils.isBlank(phoneNumber))
            throw new BadRequestException("Phone number is required for verification Option: PhoneNumber");

        if (EMAIL.equals(endUserOnBoardVerificationOption) && StringUtils.isBlank(emailAddress))
            throw new BadRequestException("Email Address is required for verification Option: EmailAddress");

        ensureEmailDoesNotExist(emailAddress);

        ensurePhoneNumberDoesNotExist(phoneNumber);


        EndUserOnboardRequest endUserOnboardRequest = EndUserOnboardRequest.builder()
                .firstName(endUserOnboardRequestDto.getFirstName())
                .lastName(endUserOnboardRequestDto.getLastName())
                .endUserOnBoardVerificationOption(endUserOnBoardVerificationOption)
                .password(passwordEncoder.encode(endUserOnboardRequestDto.getPassword()))
                .isNotificationSent(false)
                .phoneNumber(phoneNumber)
                .emailAddress(emailAddress)
                .isVerificationComplete(false)
                .verificationCode(ReferenceGenerator.generateRef(INTENT_VERIFICATION_CODE))
                .userReference(ReferenceGenerator.generateRef(INTENT_USER_REFERENCE))
                .build();

        endUserOnboardRequest.setEntityStatus(EntityStatus.PENDING);

        endUserOnboardRequest = save(endUserOnboardRequest);

        eventNotifyService.broadCastEndUserEmailNotification(endUserOnboardRequest, EndUserNotificationTemplateCode.USER_EMAIL_VERIFICATION_NOTIFICATION);

        return endUserOnboardRequest.getUserReference();
    }

    @Transactional
    public void performUserVerification(EndUserVerifyDto endUserVerifyDto) throws UnProcessableOperationException, SystemServiceException {

        EndUserOnboardRequest onboardRequest = getOnboardRequest(endUserVerifyDto);

        if (onboardRequest.isVerificationComplete())
            throw new UnProcessableOperationException("Verification already completed.");

        onboardRequest.setVerificationComplete(true);
        onboardRequest = save(onboardRequest);

        endUserService.createUser(onboardRequest);

    }

    private EndUserOnboardRequest getOnboardRequest(EndUserVerifyDto endUserVerifyDto) throws UnProcessableOperationException {
        return endUserOnboardRequestRepository.findByVerificationCodeAndUserReference(endUserVerifyDto.getVerificationCode(),endUserVerifyDto.getUserReference())
                .orElseThrow(() -> new UnProcessableOperationException("No Onboard request found for verification."));
    }

    private void ensureEmailDoesNotExist(String emailAddress) throws DuplicateEntryException {
        //todo vaidate email address format
        if (!StringUtils.isEmpty(emailAddress) && endUserOnboardRequestRepository.findIdByEmailAddress(emailAddress).isPresent()) {
            throw new DuplicateEntryException(String.format("Email address: %s is already taken.", emailAddress));
        }
    }

    private void ensurePhoneNumberDoesNotExist(String phoneNumber) throws DuplicateEntryException {

        if (!StringUtils.isEmpty(phoneNumber) && endUserOnboardRequestRepository.findIdByPhoneNumber(phoneNumber).isPresent()) {
            throw new DuplicateEntryException(String.format("Phone Number: %s is already taken.", phoneNumber));
        }
    }


}
