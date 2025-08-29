package com.acoldbottle.stockmate.event.email;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Getter
@RequiredArgsConstructor
public class EmailAlertByMapEvent {

    private final Map<String, String> failedMap;
}
