package com.acoldbottle.stockmate.external.kis;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;


@Getter
public class KisTokenReissueRes {
    @JsonProperty("access_token")
    private String token;
    @JsonProperty("access_token_token_expired")
    private String tokenExpired;
}
