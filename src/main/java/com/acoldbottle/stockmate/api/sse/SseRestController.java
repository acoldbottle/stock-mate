package com.acoldbottle.stockmate.api.sse;

import com.acoldbottle.stockmate.annotation.UserId;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/sse")
@RequiredArgsConstructor
public class SseRestController {

    private final SseService sseService;

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(@UserId Long userId) {
        return sseService.connect(userId);
    }

    @PostMapping("/disconnect")
    public void disconnect(@UserId Long userId) {
        sseService.disconnect(userId);
    }
}
