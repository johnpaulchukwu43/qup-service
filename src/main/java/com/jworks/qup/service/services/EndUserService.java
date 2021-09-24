package com.jworks.qup.service.services;

import com.jworks.app.commons.enums.EntityStatus;
import com.jworks.app.commons.enums.Role;
import com.jworks.app.commons.exceptions.*;
import com.jworks.app.commons.models.PasswordResetDto;
import com.jworks.app.commons.services.impl.ServiceBluePrintImpl;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.entities.EndUserOnboardRequest;
import com.jworks.qup.service.models.EndUserDto;
import com.jworks.qup.service.repositories.EndUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @author Johnpaul Chukwu.
 * @since 18/12/2020
 */
@Slf4j
@Service
public class EndUserService extends ServiceBluePrintImpl<EndUser, EndUserDto> implements UserDetailsService {


    private final EndUserRepository endUserRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public EndUserService(EndUserRepository endUserRepository, PasswordEncoder passwordEncoder) {
        super(endUserRepository);
        this.endUserRepository = endUserRepository;
        this.passwordEncoder = passwordEncoder;
    }


    public void createUser(EndUserOnboardRequest endUserOnboardRequest) throws UnProcessableOperationException, SystemServiceException {

        if (endUserOnboardRequest == null || !endUserOnboardRequest.isVerificationComplete()) {
            throw new UnProcessableOperationException("Cannot create end user with invalid endUserOnboardRequest Object");
        }

        EndUser endUser = EndUser.builder()
                .firstName(endUserOnboardRequest.getFirstName())
                .lastName(endUserOnboardRequest.getLastName())
                .userReference(endUserOnboardRequest.getUserReference())
                .emailAddress(endUserOnboardRequest.getEmailAddress())
                .password(endUserOnboardRequest.getPassword())
                .phoneNumber(endUserOnboardRequest.getPhoneNumber())
                .build();

        endUser.setEntityStatus(EntityStatus.ACTIVE);

        save(endUser);

        //todo send welcome email
    }

    public void resetUserPassword(PasswordResetDto passwordResetDto , String userReference) throws SystemServiceException, NotFoundRestApiException {

        EndUser endUserToUpdate = getUserByUserReference(userReference);

        String confirmPassword = passwordResetDto.getConfirmPassword();
        String password = passwordResetDto.getNewPassword();

        if(!password.equals(confirmPassword)){
            throw new BadRequestException("Passwords do not match");
        }

        String encodePassword = passwordEncoder.encode(password);

        endUserToUpdate.setPassword(encodePassword);

        update(endUserToUpdate);

    }

    @Override
    public EndUserDto convertEntityToDto(EndUser entity) {
        EndUserDto endUserDto = new EndUserDto();

        BeanUtils.copyProperties(entity, endUserDto);

        return endUserDto;
    }

    @Override
    public UserDetails loadUserByUsername(String userReference) throws UsernameNotFoundException {
        try {
            EndUser endUser = getUserByUserReference(userReference);
            //todo role is always user, implement allowance for other roles
            List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()));

            return new User(endUser.getUserReference(), endUser.getPassword(), roles);

        } catch (NotFoundRestApiException ex) {

            log.error("Exception occurred:", ex);

            throw new UsernameNotFoundException(String.format("User with reference: %s not found.", userReference));
        }
    }

    public EndUserDto getUserDtoByUserReference(String endUserReference) throws NotFoundRestApiException {

        return convertEntityToDto(getUserByUserReference(endUserReference));
    }

    public EndUser getUserByUserReference(String endUserReference) throws NotFoundRestApiException {
        return endUserRepository.findByUserReference(endUserReference)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("User with reference %s not found.", endUserReference)));
    }

}
