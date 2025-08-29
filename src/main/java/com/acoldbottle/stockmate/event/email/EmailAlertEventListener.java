package com.acoldbottle.stockmate.event.email;

import com.acoldbottle.stockmate.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailAlertEventListener {

    private final EmailService emailService;

    @EventListener
    public void sendEmailForAlertException(EmailAlertByMapEvent event) {
        Map<String, String> failedMap = event.getFailedMap();
        emailService.sendEmailAlertErrorByMap(failedMap);
    }

    @EventListener
    public void sendEmail(EmailAlertEvent event) {
        String message = event.getMessage();
        emailService.sendEmailAlertError(message);
    }
}
