package com.acoldbottle.stockmate.api.user.dto.req;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserLoginReq {

    private String username;
    private String password;
}
