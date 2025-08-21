package com.acoldbottle.stockmate.api.user.service;

import com.acoldbottle.stockmate.api.user.dto.req.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.res.UserSignUpRes;
import com.acoldbottle.stockmate.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserManager userManager;

    @Transactional
    public UserSignUpRes signUp(UserSignUpReq userSignUpReq) {
        User savedUser = userManager.create(userSignUpReq.getUsername(), userSignUpReq.getPassword(), userSignUpReq.getPasswordConfirm());
        return UserSignUpRes.from(savedUser);
    }
}
