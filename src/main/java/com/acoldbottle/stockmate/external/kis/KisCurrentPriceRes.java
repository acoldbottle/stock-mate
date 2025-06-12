package com.acoldbottle.stockmate.external.kis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.Getter;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class KisCurrentPriceRes {

    private String rt_cd;
    private Output output;

    @Getter
    public static class Output {
        private String last;
        private String rate;
    }
}
