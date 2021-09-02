package com.toby.live.lecture14;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@RestController
@Slf4j
@SpringBootApplication
public class Lecture14Application {

	public static void main(String[] args) {
		SpringApplication.run(Lecture14Application.class, args);
	}

	@GetMapping("event/{id}")
	Mono<List<Event>> hello(@PathVariable long id) {
		List<Event> list = Arrays.asList(new Event(1, "event1"), new Event(2, "event2"));
		return Mono.just(list);
	}


	// Mono와 다르게 Flux 는 Object가 여러개 가능.
	@GetMapping(value = "events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	Flux<Event> events() {
		// take : Flux fromStream  개수 제한 (10개의 response 를 보내고 cancel()을 하겠다.)
		// ex1
//		return Flux
//				.fromStream(Stream.generate(() -> new Event(System.currentTimeMillis(), "value")))
//				.delayElements(Duration.ofSeconds(1))
//				.take(10);


//		ex2
//		return Flux
////				.<Event>generate(sink -> sink.next(new Event(System.currentTimeMillis(), "value")))
//				.<Event, Long>generate(() -> 1L, (id, sink) -> {
//					sink.next(new Event(id, "value" + id));
//					return id+1;
//				})
//				.delayElements(Duration.ofSeconds(1))
//				.take(10);

		Flux<Event> f = Flux.<Event,Long>generate(() -> 1L, (id, sink) -> {
			sink.next(new Event(id, "value" + id));
			return id + 1;
		});

		Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));


		return Flux.zip(f, interval).map(tu -> tu.getT1()).take(10);
	}

	@Data @AllArgsConstructor
	public static class Event {
		long id;
		String value;
	}

}
