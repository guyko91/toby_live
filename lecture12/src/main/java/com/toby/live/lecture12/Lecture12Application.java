package com.toby.live.lecture12;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Slf4j
@EnableAsync
@SpringBootApplication
public class Lecture12Application {

	public static void main(String[] args) {
		SpringApplication.run(Lecture12Application.class, args);
	}

	// RestController 어노테이션이 있으면, return 값이 http response body에 세팅된다.
	@RestController
	public static class MyController {

		@Autowired
		MyService myservice;

		public static final String URL1 = "http://localhost:8081/service?req={req}";
		public static final String URL2 = "http://localhost:8081/service2?req={req}";

		WebClient client = WebClient.create();

		@GetMapping("/rest")
		public Mono<String> rest(int idx) {
			return client.get().uri(URL1, idx).exchange()
					.flatMap(c -> c.bodyToMono(String.class))
					.flatMap(res1 -> client.get().uri(URL2, res1).exchange())
					.flatMap(c -> c.bodyToMono(String.class))
					.doOnNext(log::info)
					.flatMap(res2 -> Mono.fromCompletionStage(myservice.work(res2)))
					.doOnNext(log::info);
		}

	}

	@Service
	public static class MyService {
		public CompletableFuture<String> work(String req) { return CompletableFuture.completedFuture(req + "/asyncwork"); }
	}

	@Bean
	ThreadPoolTaskExecutor myThreadPool() {
		ThreadPoolTaskExecutor te = new ThreadPoolTaskExecutor();
		te.setCorePoolSize(1);
		te.setMaxPoolSize(1);
		te.initialize();
		return te;
	}


}
