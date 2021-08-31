package com.toby.live.lecture11;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

@Slf4j
public class CFuture {

    public static void main(String[] args) throws ExecutionException, InterruptedException {
//        CompletableFuture
//                .runAsync(() -> log.info("runAsync"))
//                .thenRun(() -> log.info("thenRun"))
//                .thenRun(() -> log.info("thenRun2"));

        ExecutorService es = Executors.newFixedThreadPool(10);

        CompletableFuture
                .supplyAsync(() -> {
                    log.info("runAsync");
                    return 1;
                }, es)
                // stream 의 flatMap 과 같은 역할
                .thenCompose(s -> {
                    log.info("thenApply");
                    return CompletableFuture.completedFuture(s + 1);
                })
                // stream 의 map 과 같은 역할
                .thenApplyAsync(s2 -> {
                    log.info("thenApply2");
                    return s2 * 3;
                }, es)
                .exceptionally(e -> -10)
                .thenAcceptAsync(s3 -> log.info("thenAccept {}", s3), es);

        log.info("exit");

        es.shutdown();

        ForkJoinPool.commonPool().shutdown();
        ForkJoinPool.commonPool().awaitTermination(10, TimeUnit.SECONDS);
    }
}
