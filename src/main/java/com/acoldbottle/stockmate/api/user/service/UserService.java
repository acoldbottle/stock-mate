package com.acoldbottle.stockmate.api.user.service;

import com.acoldbottle.stockmate.api.user.dto.req.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.res.UserSignUpRes;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.user.UserAlreadyExistsException;
import com.acoldbottle.stockmate.exception.user.UserPasswordMismatchException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Transactional
    public UserSignUpRes signUp(UserSignUpReq userSignUpReq) {

        boolean isExists = userRepository.existsByUsername(userSignUpReq.getUsername());
        if (isExists) {
            throw new UserAlreadyExistsException(ErrorCode.USER_ALREADY_EXISTS);
        }

        if (!userSignUpReq.getPassword().equals(userSignUpReq.getPasswordConfirm())){
            throw new UserPasswordMismatchException(ErrorCode.USER_PASSWORD_MISMATCH);
        }

        User savedUser = userRepository.save(User.builder()
                .username(userSignUpReq.getUsername())
                .password(passwordEncoder.encode(userSignUpReq.getPassword()))
                .build());

        return UserSignUpRes.from(savedUser);
    }

    public UsernamePasswordAuthenticationToken createLoginToken(UserLoginReq userLoginReq) {
        return new UsernamePasswordAuthenticationToken(userLoginReq.getUsername(), userLoginReq.getPassword());
    }
}
