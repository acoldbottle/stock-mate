package com.acoldbottle.stockmate.api.watchlist.controller;

import com.acoldbottle.stockmate.annotation.UserId;
import com.acoldbottle.stockmate.api.watchlist.dto.req.WatchItemCreateReq;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemCreateRes;
import com.acoldbottle.stockmate.api.watchlist.dto.res.WatchItemGetRes;
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

@Tag(name = "Watchlist 관련 API")
public interface WatchlistAPI {

    @Operation(
            summary = "관심종목에 있는 주식 종목들 현재 시세를 가져와서 조회",
            description = "관심종목에 있는 주식 종목들을 조회합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200", description = "관심종목 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WatchItemGetRes.class),
                                    examples = @ExampleObject(value = """
                                            [
                                               {
                                                     "watchItemId": 4,
                                                     "symbol": "TSLA",
                                                     "korName": "테슬라",
                                                     "engName": "TESLA INC",
                                                     "marketCode": "NAS",
                                                     "currentPrice": 309.0800,
                                                     "rate": -0.25
                                                     },
                                                     {
                                                     "watchItemId": 3,
                                                     "symbol": "AAPL",
                                                     "korName": "애플",
                                                     "engName": "APPLE INC",
                                                     "marketCode": "NAS",
                                                     "currentPrice": 211.2500,
                                                     "rate": -0.55
                                                     }
                                            ]
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<List<WatchItemGetRes>> getWatchlist(@UserId Long userId);

    @Operation(
            summary = "관심종목에 주식 종목 추가",
            description = "관심종목에 주식 종목을 추가합니다.",
            requestBody = @RequestBody(
                    description = "추가할 주식 종목의 심볼",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = WatchItemCreateReq.class),
                            examples = @ExampleObject(value = """
                                    {
                                        "symbol": "AAPL"
                                    }
                                    """)
                    )
            ),
            responses = {
                    @ApiResponse(
                            responseCode = "201", description = "관심종목에 주식 종목 추가 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = WatchItemCreateRes.class),
                                    examples = @ExampleObject(value = """
                                            {
                                                "watchItemId": 3,
                                                "symbol": "AAPL"
                                            }
                                            """)
                            )
                    ),
                    @ApiResponse(responseCode = "400", description = "잘못된 요청 형식입니다. JSON 형식을 확인하세요.", content = @Content),
                    @ApiResponse(responseCode = "400", description = "symbol의 값이 비어있습니다.", content = @Content),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 주식을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "409", description = "이미 관심종목에 등록되어 있습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<WatchItemCreateRes> createWatchItem(@UserId Long userId,
                                                       @RequestBody @Valid WatchItemCreateReq watchItemCreateReq);

    @Operation(
            summary = "관심종목에서 주식 종목 삭제",
            description = "관심종목에서 주식 종목을 삭제합니다.",
            responses = {
                    @ApiResponse(responseCode = "204", description = "관심종목에서 삭제 성공"),
                    @ApiResponse(responseCode = "401", description = "접근 권한이 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 사용자를 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "404", description = "해당 관심종목을 찾을 수 없습니다.", content = @Content),
                    @ApiResponse(responseCode = "500", description = "서버 내부 오류입니다.", content = @Content)
            })
    ResponseEntity<Void> deleteWatchItem(@UserId Long userId,
                                         @PathVariable Long watchItemId);
}
