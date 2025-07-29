package com.acoldbottle.stockmate.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String adminEmail;

    @Async
    public void sendEmailAlertError(String text) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(adminEmail);
            mailMessage.setTo(adminEmail);
            mailMessage.setSubject("StockMate 오류");
            mailMessage.setText(text);
            mailSender.send(mailMessage);
        } catch (MailException e) {
            log.error("[EmailService] 예외 발생 ", e);
        }
    }

    @Async
    public void sendEmailAlertErrorByMap(Map<String, String> failedMap) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(adminEmail);
            mailMessage.setTo(adminEmail);
            mailMessage.setSubject("StockMate 오류");
            mailMessage.setText(createTextBody(failedMap));
            mailSender.send(mailMessage);
        } catch (MailException e) {
            log.error("[EmailService] 예외 발생 ", e);
        }
    }

    private String createTextBody(Map<String, String> failedMap) {
        StringBuilder sb = new StringBuilder();

        for (String symbol : failedMap.keySet()) {
            String errorMessage = failedMap.get(symbol);
            String content = "[" + symbol + "] -> " + errorMessage + "\n";
            sb.append(content);
        }
        return sb.toString();
    }
}

