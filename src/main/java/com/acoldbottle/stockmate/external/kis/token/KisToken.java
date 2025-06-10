package com.acoldbottle.stockmate.external.kis.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "kis_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KisToken {

    @Id
    private Long id;

    @Column(length = 1000)
    private String token;

    @Column(name = "token_expired")
    private LocalDateTime tokenExpired;

    @Builder
    public KisToken(Long id, String token, LocalDateTime tokenExpired) {
        this.id = id;
        this.token = token;
        this.tokenExpired = tokenExpired;
    }

    public void updateReissueToken(String newKisToken, LocalDateTime newTokenExpired) {
        this.token = newKisToken;
        this.tokenExpired = newTokenExpired;
    }

    public boolean isExpiringSoon() {
        return LocalDateTime.now().isAfter(this.tokenExpired.minusMinutes(10));
    }
}
