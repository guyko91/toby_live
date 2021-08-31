package com.toby.live.lecture11;

import io.netty.channel.nio.NioEventLoopGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.Netty4ClientHttpRequestFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import java.util.function.Function;

@EnableAsync
@SpringBootApplication
public class Lecture11Application {


    // RestController 어노테이션이 있으면, return 값이 http response body에 세팅된다.
    @RestController
    public static class MyController {

        RestTemplate rt = new RestTemplate();
        AsyncRestTemplate art = new AsyncRestTemplate();
        AsyncRestTemplate netty = new AsyncRestTemplate(new Netty4ClientHttpRequestFactory(new NioEventLoopGroup(1)));

        @Autowired
        MyService myservice;

        public static final String URL1 = "http://localhost:8081/service?req={req}";
        public static final String URL2 = "http://localhost:8081/service2?req={req}";

        @GetMapping("/rest")
        public DeferredResult<String> rest(int idx) {

            // RestTemplate 은 blocking 메서드라서, 서블릿 스레드가 1개인 상태에서 1개요청이 들어오면, 그 이후의 요청은 큐에 쌓인다.
//            String res = rt.getForObject("http://localhost:8081/service?req={req}", String.class, "hello" + idx);

            // AsyncRestTemlate 은 비동기 요청
            // 비동기로 처리하는 것은 많지만, 백그라운드에 스레드를 생성한다. (하나의 쓰레드로 처리된 것 처럼 보이지만, 쓰레드 100 개를 사용한다.)
//            return art.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);

            // AsyncTemplate + Netty 사용. (쓰레드를 추가로 생성하지 않고, 주어진 쓰레드로만 비동기 요청 사용)
//            return netty.getForEntity("http://localhost:8081/service?req={req}", String.class, "hello" + idx);

            // response를 받아서 가공을 하는 CASE
            DeferredResult<String> dr = new DeferredResult<>();
//            // 리팩토링 전 소스.
//            ListenableFuture<ResponseEntity<String>> f1 = netty.getForEntity(URL1, String.class, "hello" + idx);
//            // 콜백은 중첩이 가능하다.
//            f1.addCallback(
//                    s -> {
//                        ListenableFuture<ResponseEntity<String>> f2 = netty.getForEntity(URL2, String.class, s.getBody());
//                        f2.addCallback(
//                                s2 -> {
//                                    ListenableFuture<String> f3 = myservice.work(s2.getBody());
//                                    f3.addCallback(
//                                            s3 -> {
//                                                dr.setResult(s3);
//                                            },
//                                            e -> {
//                                                dr.setErrorResult(e.getMessage());
//                                            }
//                                    );},
//                                e -> {
//                                    dr.setErrorResult(e.getMessage());
//                                }
//                        );},
//                    e -> {
//                        dr.setErrorResult(e.getMessage());
//                    });

            // CompletableFuture를 활용하여 리팩토링.
            toCF(netty.getForEntity(URL1, String.class, "hello" + idx))
                    .thenCompose(s -> toCF(netty.getForEntity(URL2, String.class, s.getBody())))
                    .thenApplyAsync(s2 -> myservice.work(s2.getBody()))
                    .thenAccept(dr::setResult)
                    .exceptionally(e -> {dr.setErrorResult(e.getMessage()); return (Void)null;});

            return dr;
        }
    }

    static <T> CompletableFuture<T> toCF(ListenableFuture<T> lf) {
        CompletableFuture<T> cf = new CompletableFuture<T>();
        lf.addCallback(cf::complete, cf::completeExceptionally);
        return cf;
    }

    @Service
    public static class MyService {
        public String work(String req) { return req + "/asyncwork"; }
    }

    @Bean
    ThreadPoolTaskExecutor myThreadPool() {
        ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
        te.setCorePoolSize(1);
        te.setMaxPoolSize(1);
        te.initialize();
        return te;
    }


    public static void main(String[] args) {
        SpringApplication.run(Lecture11Application.class, args);
    }

}
