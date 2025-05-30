package com.acoldbottle.stockmate.api.user.controller;

import com.acoldbottle.stockmate.api.user.dto.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.UserLoginRes;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpRes;
import com.acoldbottle.stockmate.api.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserRestController implements UserAPI{

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    @PostMapping("/signup")
    public ResponseEntity<UserSignUpRes> signUp(@RequestBody @Valid UserSignUpReq userSignUpReq) {
        UserSignUpRes userSignUpRes = userService.signUp(userSignUpReq);
        return ResponseEntity.status(HttpStatus.CREATED).body(userSignUpRes);
    }

    @PostMapping("/login")
    public ResponseEntity<UserLoginRes> login(@RequestBody UserLoginReq userLoginReq,
                                              HttpServletRequest request) {
        UsernamePasswordAuthenticationToken token = userService.checkUserInfoForLogin(userLoginReq);
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        request.getSession(true).setAttribute("SPRING_SECURITY_CONTEXT", securityContext);
        return ResponseEntity.status(HttpStatus.OK).body(new UserLoginRes(userLoginReq.getUsername()));
    }



    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
