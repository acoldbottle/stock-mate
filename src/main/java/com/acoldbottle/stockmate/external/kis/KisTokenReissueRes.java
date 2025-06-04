package com.acoldbottle.stockmate.external.kis;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class KisTokenReissueRes {

    private String token;
    private LocalDateTime tokenExpired;
}
