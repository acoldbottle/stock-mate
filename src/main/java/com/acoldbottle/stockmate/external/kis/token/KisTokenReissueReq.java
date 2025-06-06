package com.acoldbottle.stockmate.external.kis.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KisTokenReissueReq {

    private String grant_type;
    private String appkey;
    private String appsecret;
}
