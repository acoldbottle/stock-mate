package com.acoldbottle.stockmate.scheduler;

import com.acoldbottle.stockmate.event.email.EmailAlertEvent;
import com.acoldbottle.stockmate.external.kis.token.KisTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class KisTokenScheduler {

    private final KisTokenService kisTokenService;
    private final ApplicationEventPublisher eventPublisher;

    @Scheduled(cron = "0 0 6,15 * * *", zone = "Asia/Seoul")
    public void reissueToken() {
        try {
            kisTokenService.reissueToken();
        } catch (Exception e) {
            log.error("=== [KisTokenScheduler] 토큰 재발급 실패 ===", e);
            eventPublisher.publishEvent(new EmailAlertEvent("[KisTokenScheduler] 예외 발생 --> " + e.getMessage()));
        }
    }
}
