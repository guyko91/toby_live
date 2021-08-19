package toby;


import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class Application6 {

    @RestController
    public static class Controller {
        @RequestMapping("/hello")
        public Publisher<String> hello(String name) {
            return new Publisher<String>() {
                @Override
                public void subscribe(Subscriber<? super String> sub) {
                    sub.onSubscribe(new Subscription() {
                        @Override
                        public void request(long n) {
                            sub.onNext("Hello : " + name);
                            sub.onComplete();
                        }
                        @Override
                        public void cancel() {

                        }
                    });
                }
            };
        }
    }

    public static void main(String[] args) {
        SpringApplication.run(Application6.class, args);
    }
}
