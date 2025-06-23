package com.acoldbottle.stockmate.api.holding.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingCreateReq;
import com.acoldbottle.stockmate.api.holding.dto.req.HoldingUpdateReq;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingCreateRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingGetWithProfitRes;
import com.acoldbottle.stockmate.api.holding.dto.res.HoldingUpdateRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Tag(name = "Holding 관련 API")
public interface HoldingAPI {

    @Operation(
            summary = "포트폴리오 안에 있는 주식 종목들 현재시세를 가져와서 조회",
            description = "포트폴리오 안에 있는 주식 종목들을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "주식 종목들 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HoldingGetWithProfitRes.class),
                                    examples = @ExampleObject(value = """
                                            [
                                                {
                                                    "symbol": "AAPL",
                                                    "marketCode": "NAS",
                                                    "avgPurchasePrice": 150.88,
                                                    "quantity": 1,
                                                    "totalAmount": 150.88,
                                                    "currentPrice": 201.0000,
                                                    "rate": 2.25,
                                                    "profitAmount": 50.1200,
                                                    "profitRate": 33.218500
                                                },
                                                {
                                                    "symbol": "NVDA",
                                                    "marketCode": "NAS",
                                                    "avgPurchasePrice": 140.04,
                                                    "quantity": 100,
                                                    "totalAmount": 14004.00,
                                                    "currentPrice": 143.8500,
                                                    "rate": -1.12,
                                                    "profitAmount": 381.0000,
                                                    "profitRate": 2.720700
                                                }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 주식을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<List<HoldingGetWithProfitRes>> getHoldingListWithProfit(@UserId Long userId,
                                                                           @PathVariable Long portfolioId);
    @Operation(
            summary = "가지고 있는 주식 종목 포트폴리오에 등록",
            description = "사용자의 포트폴리오에 주식 종목을 등록합니다.",
            requestBody = @RequestBody(
                    description = "매수한 주식의 심볼, 매수가, 수량",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HoldingCreateReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "symbol" : "AAPL",
                                        "quantity" : 10,
                                        "purchasePrice" : 120.88
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "사용자 주식 등록 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HoldingCreateRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "id": 1,
                                                "symbol": "AAPL",
                                                "quantity": 10,
                                                "purchasePrice": 120.88
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. JSON 형식을 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "symbol의 값이 비어있습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "수량은 0보다 커야합니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "매수가는 0보다 커야합니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "숫자 값이 한계를 초과합니다(<10 자리>.<2 자리> 예상)", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 주식을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<HoldingCreateRes> createHolding(@UserId Long userId,
                                                   @PathVariable Long portfolioId,
                                                   @RequestBody @Valid HoldingCreateReq holdingCreateReq);

    @Operation(
            summary = "포트폴리오에 있는 주식 수정",
            description = "주식의 매수가, 수량을 수정합니다.",
            requestBody = @RequestBody(
                    description = "수정할 주식의 매수가, 수량",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HoldingUpdateReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "quantity" : 8,
                                        "purchasePrice" : 111.88
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "포트폴리오 내에 있는 주식 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HoldingUpdateRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "id": 1,
                                                "symbol": "AAPL",
                                                "quantity": 8,
                                                "purchasePrice": 111.88
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. JSON 형식을 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "수량은 0보다 커야합니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "매수가는 0보다 커야합니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "숫자 값이 한계를 초과합니다(<10 자리>.<2 자리> 예상)", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 주식을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 보유 종목을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<HoldingUpdateRes> updateHolding(@UserId Long userId,
                                                   @PathVariable Long portfolioId,
                                                   @PathVariable Long holdingId,
                                                   @RequestBody @Valid HoldingUpdateReq holdingUpdateReq);

    @Operation(
            summary = "포트폴리오에 있는 주식 종목 삭제",
            description = "포트폴리오에서 원하는 주식 종목을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "주식 종목 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 포트폴리오를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 주식을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 보유 종목을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<Void> deleteHolding(@UserId Long userId,
                                       @PathVariable Long portfolioId,
                                       @PathVariable Long holdingId);
}
