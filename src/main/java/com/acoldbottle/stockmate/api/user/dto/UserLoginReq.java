package com.acoldbottle.stockmate.api.user.dto;

import lombok.Data;

@Data
public class UserLoginReq {

    private String username;
    private String password;
}
