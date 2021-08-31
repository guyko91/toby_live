package com.toby.live.lecture7;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Slf4j
public class FluxEx {

    public static void main(String[] args) throws InterruptedException {
        // EX1
//        Flux.range(1, 10)
//                .publishOn(Schedulers.newSingle("pub"))
//                .log()
//                .subscribeOn(Schedulers.newSingle("sub"))
//                .subscribe(System.out::println);
//
//        System.out.println("Exit");

        // EX2
        Flux.interval(Duration.ofMillis(500))
                .subscribe(s -> log.debug("onNext : {}", s));

        System.out.println("exit");
        TimeUnit.SECONDS.sleep(5);
    }
}
