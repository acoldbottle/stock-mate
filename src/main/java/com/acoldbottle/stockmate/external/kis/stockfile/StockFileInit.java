package com.acoldbottle.stockmate.external.kis.stockfile;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StockFileInit {

    private final StockFileDownloader stockFileDownloader;

    @PostConstruct
    public void init() {
        stockFileDownloader.downloadAll();
    }
}
