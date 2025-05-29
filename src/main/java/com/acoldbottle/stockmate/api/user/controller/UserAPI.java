package com.acoldbottle.stockmate.api.user.controller;

import com.acoldbottle.stockmate.api.user.dto.UserLoginReq;
import com.acoldbottle.stockmate.api.user.dto.UserLoginRes;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpReq;
import com.acoldbottle.stockmate.api.user.dto.UserSignUpRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@Tag(name = "User 관련 API")
public interface UserAPI {
    @Operation(
            summary = "회원가입",
            description = "사용자가 회원가입을 합니다.",
            requestBody = @RequestBody(
                    description = "회원가입 할 아이디, 비밀번호",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserSignUpReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "username" : "user0001",
                                        "password" : "a12345678",
                                        "passwordConfirm" : "a12345678"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "회원가입 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserSignUpRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "id" : 1,
                                                "username" : "user0001"
                                            }
                                            """)

                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "비밀번호가 일치하지 않습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "회원 아이디는 5자 이상, 15자 이하여야 합니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "회원 비밀번호는 8자 이상이어야 합니다.", content = @Content),
                    @ApiResponse(responseCode = "409", description = "이미 존재하는 회원 아이디입니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            }
    )
    ResponseEntity<UserSignUpRes> signUp(@RequestBody @Valid UserSignUpReq userSignUpReq);

    @Operation(
            summary = "로그인",
            description = "사용자가 로그인을 합니다.",
            requestBody = @RequestBody(
                    description = "로그인할 아이디, 비밀번호",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UserLoginReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "username" : "user0001",
                                        "password" : "a12345678"
                                    }
                                    """)
                    )

            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "로그인 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserLoginRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "username" : "user0001"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "아이디 또는 비밀번호가 잘못되었습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            }
    )
    ResponseEntity<UserLoginRes> login(@RequestBody UserLoginReq userLoginReq);

    @Operation(
            summary = "로그아웃",
            description = "사용자가 로그아웃 합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            }
    )
    ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response);
}
