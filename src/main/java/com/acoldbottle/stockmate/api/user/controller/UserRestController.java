package com.acoldbottle.stockmate.api.user.controller;

import com.acoldbottle.stockmate.api.user.dto.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.UserLoginRes;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.UserService;
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
public class UserRestController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpRes> signUp(@RequestBody @Valid UserSignUpReq userSignUpReq) {

        UserSignUpRes userSignUpRes = userService.signUp(userSignUpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpRes);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@RequestBody UserLoginReq userLoginReq) {

        UserLoginRes userLoginRes = userService.login(userLoginReq);
        return ResponseEntity.status(HttpStatus.OK).body(userLoginRes);
    }

}
