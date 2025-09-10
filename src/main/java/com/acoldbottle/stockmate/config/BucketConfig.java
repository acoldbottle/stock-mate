package com.acoldbottle.stockmate.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BucketConfig {

    @Bean
    public Bucket bucket() {
        Refill refill = Refill.greedy(12, Duration.ofMillis(1000));
        Bandwidth limit = Bandwidth.classic(12, refill);

        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}
