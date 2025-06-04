package com.acoldbottle.stockmate.external.kis;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "kis_token")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KisToken {

    @Id
    private Long id = 1L;

    private String token;

    @Column(name = "token_expired")
    private LocalDateTime tokenExpired;
}
