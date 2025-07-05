package com.crm.auth.service;

import com.crm.auth.dto.request.SignInRequest;
import com.crm.auth.dto.request.SignUpRequest;
import com.crm.auth.dto.request.UserRequest;
import com.crm.auth.mapper.UserMapper;
import com.crm.auth.persistance.entity.User;
import com.crm.auth.persistance.repository.UserRepository;
import com.crm.sharedlib.exception.ConflictException;
import com.crm.sharedlib.exception.ForbiddenException;
import com.crm.sharedlib.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder encoder;

    private final UserMapper userMapper;

    @Transactional
    public User createUser(SignUpRequest userRequest) {
        User user = userMapper.toEntity(userRequest);

        user.setPassword(encoder.encode(userRequest.getPassword()));

        if (userRepository.findByLogin(userRequest.getLogin()).isPresent()) {
            throw new ConflictException("User with login %s already exists."
                    .formatted(userRequest.getLogin())
            );
        }

        return userRepository.save(user);
    }

    public User validateUserForSignInRequest(SignInRequest request) {
        User user = userRepository.findByLogin(request.getLogin())
                .orElseThrow(() -> new ForbiddenException("Wrong login or password"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new ForbiddenException("Wrong login or password");
        }

        return user;
    }

    public List<User> getUsersByFullNameStartsWith(String fullName) {
        return userRepository.findByFullNameStartingWithIgnoreCase(fullName);
    }

    public User getUserByIdOrThrowException(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User is not found"));
    }

    @Transactional
    public User updateUser(Long userId, UserRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setAboutYourself(request.getAboutYourself());

        return user;
    }

}
