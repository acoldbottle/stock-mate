package com.acoldbottle.stockmate.api.user.service;

import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.user.UserAlreadyExistsException;
import com.acoldbottle.stockmate.exception.user.UserNotFoundException;
import com.acoldbottle.stockmate.exception.user.UserPasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import static com.acoldbottle.stockmate.exception.ErrorCode.*;

@Component
@RequiredArgsConstructor
public class UserManager {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public User get(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    public User create(String username, String password, String passwordConfirm) {
        boolean exists = userRepository.existsByUsername(username);
        if (exists) {
            throw new UserAlreadyExistsException(USER_ALREADY_EXISTS);
        }

        if (!password.equals(passwordConfirm)){
            throw new UserPasswordMismatchException(ErrorCode.USER_PASSWORD_MISMATCH);
        }

        return userRepository.save(
                User.builder()
                        .username(username)
                        .password(passwordEncoder.encode(password))
                        .build());
    }
}
