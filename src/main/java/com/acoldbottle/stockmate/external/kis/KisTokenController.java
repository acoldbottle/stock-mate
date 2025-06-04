package com.acoldbottle.stockmate.external.kis;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class KisTokenController {

    private final KisTokenService kisTokenService;

    @PostMapping("/kis/token/reissue")
    public ResponseEntity<Void> reissueToken() {
        kisTokenService.reissueToken();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
