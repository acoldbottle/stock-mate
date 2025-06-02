package com.acoldbottle.stockmate.user;

import com.acoldbottle.stockmate.api.user.dto.req.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.res.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.UserService;
import com.acoldbottle.stockmate.exception.user.UserAlreadyExistsException;
import com.acoldbottle.stockmate.exception.user.UserPasswordMismatchException;
import jakarta.validation.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
public class UserServiceTest {

    @Autowired
    UserService userService;
    @Autowired
    Validator validator;
    @Autowired
    AuthenticationManager authenticationManager;

    @Test
    @Transactional
    @DisplayName("사용자 회원 가입 성공")
    void signUp_success() {
        UserSignUpReq userData = UserSignUpReq.builder()
                .username("user_test")
                .password("12345678")
                .passwordConfirm("12345678")
                .build();

        UserSignUpRes savedUser = userService.signUp(userData);

        assertThat(userData.getUsername()).isEqualTo(savedUser.getUsername());
    }

    @Test
    @Transactional
    @DisplayName("사용자 회원 가입 실패 -> 아이디 중복 예외 발생")
    void signUp_failed_user_already() {
        UserSignUpReq userData1 = UserSignUpReq.builder()
                .username("user_test")
                .password("12345678")
                .passwordConfirm("12345678")
                .build();

        UserSignUpRes savedUser = userService.signUp(userData1);

        UserSignUpReq userData2 = UserSignUpReq.builder()
                .username("user_test")
                .password("12345678")
                .passwordConfirm("12345678")
                .build();

        assertThatThrownBy(() ->
                userService.signUp(userData2))
                .isInstanceOf(UserAlreadyExistsException.class);
    }

    @Test
    @Transactional
    @DisplayName("사용자 회원 가입 실패 -> 패스워드 불일치")
    void signUp_failed_password_mismatch() {

        UserSignUpReq userData = UserSignUpReq.builder()
                .username("user_test")
                .password("12345123")
                .passwordConfirm("23456123")
                .build();

        assertThatThrownBy(() ->
                userService.signUp(userData))
                .isInstanceOf(UserPasswordMismatchException.class);
    }

    @Test
    @Transactional
    @DisplayName("사용자 회원 가입 실패 -> 아이디, 패스워드 사이즈 충족X")
    void signUp_failed_username_size() {

        UserSignUpReq userData = UserSignUpReq.builder()
                .username("user")
                .password("12345")
                .passwordConfirm("12345")
                .build();

        assertThat(validator.validate(userData).size()).isEqualTo(2);
    }

    @Test
    @DisplayName("사용자 로그인 성공")
    void login_success() {

        UserSignUpReq userData = UserSignUpReq.builder()
                .username("user_test")
                .password("12345678")
                .passwordConfirm("12345678")
                .build();

        UserSignUpRes savedUser = userService.signUp(userData);

        UsernamePasswordAuthenticationToken token = userService.createLoginToken(UserLoginReq.builder()
                .username(userData.getUsername())
                .password(userData.getPassword())
                .build());

        Authentication authentication = authenticationManager.authenticate(token);

        assertThat(authentication.getName()).isEqualTo(savedUser.getUsername());
    }

    @Test
    @DisplayName("사용자 로그인 실패 -> 아이디, 패스워드 불일치")
    void login_failed() {
        UserSignUpReq userData = UserSignUpReq.builder()
                .username("user_test")
                .password("12345678")
                .passwordConfirm("12345678")
                .build();

        UserSignUpRes savedUser = userService.signUp(userData);

        UsernamePasswordAuthenticationToken token = userService.createLoginToken(UserLoginReq.builder()
                .username("user_testtest")
                .password("1234567800")
                .build());

        assertThatThrownBy(() ->
                authenticationManager.authenticate(token))
                .isInstanceOf(BadCredentialsException.class);
    }
}
