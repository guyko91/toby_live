package toby;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.Callable;

@SpringBootApplication
@Slf4j
@EnableAsync
public class WebApplication8 {

    /* Servlet 3.0 : 비동기 서블릿
        - Http connection은 이미 논블록킹 IO
        - 서블릿 요청 읽기, 응답 쓰기는 블록킹
        - 비동기 작업 시작 즉시 서블릿 쓰레드 반납
        - 비동기 작업이 완료되면 서블릿 쓰레드 재할당
        - 비동기 서블릿 컨텍스트 이용 (AsyncContext)
     */

    /**
     * Servlet 3.1 : 논블록킹 IO
     * 논블록킹 서블릿 요청, 응답 처리.
     * Callback
     */

    @RestController
    public static class MyController {

        @GetMapping("/async")
        public String async() throws InterruptedException {
            Thread.sleep(2000);
            return "hello";
        }

        @GetMapping("/callable")
        public Callable<String> callable() {
            log.info("callable");
            return () -> {
                log.info("async");
                Thread.sleep(2000);
                return "hello";
            };
        }
//        public String callable() throws InterruptedException {
//                log.info("async");
//                Thread.sleep(2000);
//                return "hello";
//        }

    }

    public static void main(String[] args) {
        SpringApplication.run(WebApplication8.class, args);
    }
}
