package com.acoldbottle.stockmate.api.user.dto.res;

import com.acoldbottle.stockmate.domain.user.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserSignUpRes {

    private Long id;
    private String username;

    public static UserSignUpRes from(User user) {

        return UserSignUpRes.builder()
                .id(user.getId())
                .username(user.getUsername())
                .build();
    }
}
