package com.toby.live.lecture13;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@SpringBootApplication
public class Lecture13Application {

    @GetMapping("/")
    Mono<String> rest() {
        log.info("pos1");

//        Mono<String> m = Mono.just(generateHello())
//                .doOnNext(log::info)
//                .log();

        Mono<String> m = Mono.fromSupplier(this::generateHello)
                .doOnNext(log::info)
                .log();

        m.subscribe();

        log.info("pos2");
        return m;
    }

    private String generateHello() {
        log.info("method generateHello()");
        return "Hello Mono";
    }

    public static void main(String[] args) {
        SpringApplication.run(Lecture13Application.class, args);
    }

}
