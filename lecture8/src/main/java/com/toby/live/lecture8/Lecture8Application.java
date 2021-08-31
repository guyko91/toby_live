package com.toby.live.lecture8;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyEmitter;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.Executors;

@Slf4j
@EnableAsync
@SpringBootApplication
@RestController
public class Lecture8Application {


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


	Queue<DeferredResult<String>> results = new ConcurrentLinkedDeque<>();

	@GetMapping("/dr")
	public DeferredResult<String> dr() {
		log.info("dr");
		DeferredResult<String> dr = new DeferredResult<>(600000L);
		results.add(dr);
		return dr;
	}

	@GetMapping("/dr/count")
	public String drCount() {
		return String.valueOf(results.size());
	}

	@GetMapping("/dr/event")
	public String drEvent(String msg) {
		for(DeferredResult<String> dr : results) {
			dr.setResult("Hello " + msg);
			results.remove(dr);
		}
		return "OK";
	}

	// 스트리밍 방식으로 response
	@GetMapping("/emitter")
	public ResponseBodyEmitter emitter() throws InterruptedException {
		ResponseBodyEmitter emitter = new ResponseBodyEmitter();

		Executors.newSingleThreadExecutor().submit(() -> {
			for(int i = 0; i <= 50; i++) {
				try {
					emitter.send("<p>Stream " + i + "</p>");
					Thread.sleep(100);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		return emitter;
	}

	public static void main(String[] args) {
		SpringApplication.run(Lecture8Application.class, args);
	}

}
