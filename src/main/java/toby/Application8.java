package toby;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@SpringBootApplication
@Slf4j
@EnableAsync
public class Application8 {

    // 스프링에서 비동기를 처리하는 방법.
    // @EnableAsync + @Async
    // 이것 조차 10년 전에 사용하던 기술.

    @Component
    public static class MyService {
        @Async
        public Future<String> hello() throws InterruptedException {
            log.info("hello()");
            Thread.sleep(2000);
            return new AsyncResult<>("Hello");
        }
    }

    public static void main(String[] args) {
        try(ConfigurableApplicationContext c = SpringApplication.run(Application8.class, args)) {}
    }

    @Autowired MyService myService;

    @Bean
    ApplicationRunner run() {
        return args -> {
            log.info("run()");
            Future<String> f = myService.hello();
            log.info("exit : " + f.isDone());
            log.info("result : " + f.get());
        };
    }

}
