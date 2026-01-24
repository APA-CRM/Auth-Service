package com.crm.auth.service;

import com.crm.auth.persistance.entity.PasswordRestoreRequest;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.persistance.repository.PasswordRestoreRequestRepository;
import com.crm.auth.utils.VerificationCodeGenerator;
import com.crm.sharedlib.core.exception.ConflictException;
import com.crm.sharedlib.core.exception.ForbiddenException;
import com.crm.sharedlib.core.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordRestoreRequestService {

    private final PasswordRestoreRequestRepository repository;

    @Value("${app.restore-password-request.max-attempts}")
    private Integer maxAttemptsCount;

    @Value("${app.restore-password-request.ttl}")
    private Integer restoreRequestTtl;

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
            PasswordRestoreRequest restoreRequest, String verificationCode
    ) {
        if (restoreRequest.getAttemptsCount() >= maxAttemptsCount) {
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

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void checkExistedRequestIfExpiredDeleteOrThrowException(User user) {
        Optional<PasswordRestoreRequest> restoreRequestOptional = repository.findByUser(user);

        if (restoreRequestOptional.isEmpty()) {
            return;
        }

        PasswordRestoreRequest existedRestoreRequest = restoreRequestOptional.get();

        boolean isNotExpired = existedRestoreRequest.getCreateAt()
                .plusSeconds(restoreRequestTtl).isAfter(Instant.now());

        if (isNotExpired) {
            throw new ConflictException("Password restore request already exists for this user. Try again later");
        }

        repository.delete(existedRestoreRequest);
    }

}
