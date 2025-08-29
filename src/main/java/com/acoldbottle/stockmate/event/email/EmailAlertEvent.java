package com.acoldbottle.stockmate.event.email;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class EmailAlertEvent {

    private final String message;
}
