package com.acoldbottle.stockmate.api.user.controller;

import com.acoldbottle.stockmate.api.user.dto.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
}
