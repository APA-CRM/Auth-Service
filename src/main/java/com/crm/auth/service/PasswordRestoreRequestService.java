package com.crm.auth.service;

import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.persistance.repository.PasswordRestoreRequestRepository;
import com.crm.auth.utils.VerificationCodeGenerator;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRestoreRequestService {

    private final PasswordRestoreRequestRepository repository;
    @Value("${app.max-restore-password-attempts}")
    private Integer maxAttemptsCount;

    public PasswordRestoreRequest getByIdOrThrowException(UUID requestId) {
        return repository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Password restore request is not found"));
    }

    @Transactional
    public PasswordRestoreRequest createPasswordRestoreRequest(User user) {
        PasswordRestoreRequest restoreRequest = new PasswordRestoreRequest();

        restoreRequest.setUser(user);
        restoreRequest.setVerificationCode(
                VerificationCodeGenerator.generateRandom4DigitVerificationCode()
        );

        return repository.save(restoreRequest);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean checkVerificationCodeForRestoreRequest(
            PasswordRestoreRequest restoreRequest, Integer verificationCode
    ) {
        if (restoreRequest.getAttemptsCount() > maxAttemptsCount) {
            throw new ForbiddenException("Max attempts count excided");
        }

        boolean verificationCodeIsCorrect = restoreRequest.getVerificationCode().equals(verificationCode);

        if (!verificationCodeIsCorrect) {
            restoreRequest.incrementAttemptsCount();

            repository.save(restoreRequest);
        }

        return verificationCodeIsCorrect;
    }

    @Transactional
    public void deletePasswordRestoreRequest(PasswordRestoreRequest restoreRequest) {
        repository.delete(restoreRequest);
    }


}
