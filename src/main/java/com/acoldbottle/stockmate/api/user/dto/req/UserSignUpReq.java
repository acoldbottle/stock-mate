package com.acoldbottle.stockmate.api.user.dto.req;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class UserSignUpReq {

    @NotBlank
    @Size(min = 5, max = 15, message = "회원 아이디는 5자 이상, 15자 이하여야 합니다.")
    private String username;

    @NotBlank
    @Size(min = 8, message = "회원 비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank
    private String passwordConfirm;
}
