package com.jworks.qup.service.services;

import com.jworks.app.commons.enums.Role;
import com.jworks.app.commons.exceptions.BadRequestException;
import com.jworks.app.commons.exceptions.NotFoundRestApiException;
import com.jworks.app.commons.models.AuthenticationResponse;
import com.jworks.qup.service.entities.EndUser;
import com.jworks.qup.service.enums.LoginType;
import com.jworks.qup.service.models.EndUserLoginDto;
import com.jworks.qup.service.repositories.EndUserRepository;
import com.jworks.qup.service.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.jworks.qup.service.enums.LoginType.EMAIL;
import static com.jworks.qup.service.enums.LoginType.PHONE_NUMBER;

/**
 * @author Johnpaul Chukwu.
 * @since 16/04/2021
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class EndUserAuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final EndUserRepository endUserRepository;

    public AuthenticationResponse authenticateUser(EndUserLoginDto endUserLoginDto) throws  BadRequestException {

        LoginType loginType = LoginType.valueOf(endUserLoginDto.getLoginType());
        String phoneNumber = endUserLoginDto.getPhoneNumber();
        String emailAddress = endUserLoginDto.getEmailAddress();
        String password = endUserLoginDto.getPassword();

        if (PHONE_NUMBER.equals(loginType) && StringUtils.isBlank(phoneNumber))
            throw new BadRequestException("Phone number is required for loginType: PhoneNumber");

        if (EMAIL.equals(loginType) && StringUtils.isBlank(emailAddress))
            throw new BadRequestException("Email Address is required for loginType: EmailAddress");

        String userReference;

        try {
            if (PHONE_NUMBER.equals(loginType)) {
                userReference = getUserRefByPhoneNumber(phoneNumber).getUserReference();
            } else {
                userReference = getUserRefByEmail(emailAddress).getUserReference();
            }

            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(userReference, password)
            );

            //todo role is always user, implement allowance for other roles
            List<SimpleGrantedAuthority> roles = Collections.singletonList(new SimpleGrantedAuthority(Role.USER.name()));

            User user =  new User(userReference, password, roles);

            return new AuthenticationResponse(jwtUtil.generateToken(user));

        } catch (NotFoundRestApiException | BadCredentialsException ex) {
            log.error("Invalid login credentials", ex);
            throw new BadRequestException(String.format("Invalid %s / password.", endUserLoginDto.getLoginType()));
        }
    }

    private EndUser getUserRefByEmail(String emailAddress) throws NotFoundRestApiException {
        return endUserRepository.findByEmailAddress(emailAddress)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("User with emailAddress: %s not found", emailAddress)));
    }


    private EndUser getUserRefByPhoneNumber(String phoneNumber) throws NotFoundRestApiException {
        return endUserRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new NotFoundRestApiException(String.format("User with phone number: %s not found", phoneNumber)));
    }
}
