package com.acoldbottle.stockmate.api.portfolio.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioCreateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.req.PortfolioUpdateReq;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioCreateRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioGetRes;
import com.acoldbottle.stockmate.api.portfolio.dto.res.PortfolioUpdateRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.parameters.RequestBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Portfolio 관련 API")
public interface PortfolioAPI {

    @Operation(
            summary = "포트폴리오 리스트 조회",
            description = "사용자의 포트폴리오 리스트를 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "포트폴리오 리스트 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PortfolioGetRes.class),
                                    examples = @ExampleObject(value = """
                                            [
                                                {
                                                    "portfolioId": 8,
                                                    "title": "my third portfolio"
                                                },
                                                {
                                                    "portfolioId": 5,
                                                    "title": "my second portfolio"
                                                },
                                                {
                                                    "portfolioId": 1,
                                                    "title": "my first portfolio"
                                                }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<List<PortfolioGetRes>> getPortfolioList(@UserId Long userId);

    @Operation(
            summary = "포트폴리오 생성",
            description = "사용자가 포트폴리오를 생성합니다.",
            requestBody = @RequestBody(
                    description = "생설할 포트폴리오의 이름",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PortfolioCreateReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "title" : "my first portfolio"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "포트폴리오 생성 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PortfolioCreateRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "portfolioId" : 1,
                                                "title" : "my first portfolio"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "공백일 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. JSON 형식을 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<PortfolioCreateRes> createPortfolio(@UserId Long userId,
                                                       @RequestBody @Valid PortfolioCreateReq portfolioCreateReq);

    @Operation(
            summary = "포트폴리오 수정",
            description = "사용자가 포트폴리오를 수정합니다.",
            requestBody = @RequestBody(
                    description = "수정할 포트폴리오의 새 이름",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = PortfolioUpdateReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "title" : "first Portfolio"
                                    }
                                    """)
                    )

            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "포트폴리오 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = PortfolioUpdateRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "portfolioId" : 1,
                                                "title" : "first Portfolio"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "공백일 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "필드가 잘 못 되었습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. JSON 형식을 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<PortfolioUpdateRes> updatePortfolioTitle(@UserId Long userId,
                                                            @PathVariable Long portfolioId,
                                                            @RequestBody @Valid PortfolioUpdateReq portfolioUpdateReq);

    @Operation(
            summary = "포트폴리오 삭제",
            description = "사용자가 포트폴리오를 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "포트폴리오 삭제 성공"),
                    @ApiResponse(responseCode = "400", description = "필드가 잘 못 되었습니다.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<Void> deletePortfolio(@UserId Long userId,
                                         @PathVariable @NotNull Long portfolioId);

}
