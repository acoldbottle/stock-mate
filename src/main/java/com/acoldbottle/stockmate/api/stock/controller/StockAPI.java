package com.acoldbottle.stockmate.api.stock.controller;

import com.acoldbottle.stockmate.api.stock.dto.res.StockSearchRes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Stock 관련 API")
public interface StockAPI {

    @Operation(
            summary = "키워드로 주식 검색",
            description = "키워드로 시작하는 주식을 검색하고 리스트로 반환합니다.",
            parameters = @Parameter(
                    name = "keyword",
                    description = "검색 키워드 (예: 'apple', '애플', 'APPL')",
                    required = true,
                    example = "apple"
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "주식 검색 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = StockSearchRes.class),
                                    examples = @ExampleObject(value = """
                                            [
                                                {
                                                    "symbol": "AAPL",
                                                    "korName": "애플",
                                                    "engName": "APPLE INC",
                                                    "marketCode": "nasdaq"
                                                },
                                                {
                                                    "symbol": "APLE",
                                                    "korName": "애플 호스피털리티 리츠",
                                                    "engName": "APPLE HOSPITALITY REIT INC",
                                                    "marketCode": "nyse"
                                                }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "검색어는 공백일 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. 파라미터를 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<List<StockSearchRes>> searchStocks(@RequestParam @NotBlank String keyword);

    ResponseEntity<Void> updateStockDB();
}
