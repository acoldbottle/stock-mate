package com.acoldbottle.stockmate.external.kis;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class KisCurrentPriceReq {

    private String Authorization;
    private String appkey;
    private String appsecret;
    private String custtype;
    private String tr_id;
}
