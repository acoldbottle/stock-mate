package com.acoldbottle.stockmate.api.user.service;

import com.acoldbottle.stockmate.api.user.dto.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.UserLoginRes;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpRes;
import com.acoldbottle.stockmate.domain.user.User;
import com.acoldbottle.stockmate.domain.user.UserRepository;
import com.acoldbottle.stockmate.exception.ErrorCode;
import com.acoldbottle.stockmate.exception.user.UserAlreadyExistsException;
import com.acoldbottle.stockmate.exception.user.UserPasswordMismatchException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.manager.util.SessionUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

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

    public UserLoginRes login(UserLoginReq userLoginReq) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userLoginReq.getUsername(), userLoginReq.getPassword());

        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new UserLoginRes(userLoginReq.getUsername());
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
    }
}
