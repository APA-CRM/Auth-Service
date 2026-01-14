package com.crm.auth.service.operation;

import com.crm.auth.dto.request.RestorePasswordRequest;
import com.crm.auth.dto.request.VerificationCodeRequest;
import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.service.PasswordRestoreRequestService;
import com.crm.auth.service.UserService;
import com.crm.auth.service.producer.SendVerificationCodeProducer;
import com.crm.sharedlib.core.exception.ForbiddenException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRecoveryService {

    private final PasswordRestoreRequestService passwordRestoreRequestService;
    private final UserService userService;

    private final SendVerificationCodeProducer verificationCodeSender;

    @Transactional
    public PasswordRestoreRequest createPasswordRestoreRequest(RestorePasswordRequest restorePasswordRequest) {
        User user = userService
                .getUserByEmailOrThrowException(restorePasswordRequest.getEmail());

        PasswordRestoreRequest passwordRestoreRequest =
                passwordRestoreRequestService.createPasswordRestoreRequest(user);

        verificationCodeSender.sendVerificationCode(user.getEmail(), passwordRestoreRequest.getVerificationCode());

        return passwordRestoreRequest;
    }

    @Transactional
    public User checkVerificationCodeAndGetUser(UUID requestId, VerificationCodeRequest request) {
        PasswordRestoreRequest restoreRequest =
                passwordRestoreRequestService.getByIdOrThrowException(requestId);

        boolean isCorrectVerificationCode =
                passwordRestoreRequestService
                        .checkVerificationCodeForRestoreRequest(restoreRequest, request.getVerificationCode());

        if (!isCorrectVerificationCode) {
            throw new ForbiddenException("Invalid verification code");
        }

        passwordRestoreRequestService.deletePasswordRestoreRequest(restoreRequest);

        return restoreRequest.getUser();
    }

}
