package com.toby.live.lecture10;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class RemoteService {

    // RestController 어노테이션이 있으면, return 값이 http response body에 세팅된다.
    @RestController
    public static class MyController {

        @GetMapping("/service")
        public String service(String req) throws InterruptedException {
            Thread.sleep(1000);
            throw new RuntimeException();
//            return req + "/service1";
        }

        @GetMapping("/service2")
        public String service2(String req) throws InterruptedException {
            Thread.sleep(1000);
            return req + "/service2";
        }
    }

    public static void main(String[] args) {

        System.setProperty("server.port", "8081");
        System.setProperty("server.tomcat.threads.max", "1000");

        SpringApplication.run(RemoteService.class, args);
    }

}
