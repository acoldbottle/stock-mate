package com.acoldbottle.stockmate.api.user.controller;

import com.acoldbottle.stockmate.api.user.dto.req.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.res.UserLoginRes;
import com.acoldbottle.stockmate.api.user.dto.res.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.AuthService;
import com.acoldbottle.stockmate.api.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController implements UserAPI{

    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpRes> signUp(@RequestBody @Valid UserSignUpReq userSignUpReq) {
        UserSignUpRes userSignUpRes = userService.signUp(userSignUpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpRes);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@RequestBody UserLoginReq userLoginReq,
                                              HttpServletRequest request) {
        UserLoginRes userLoginRes = authService.login(userLoginReq, request);
        return ResponseEntity.status(HttpStatus.OK).body(userLoginRes);
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
