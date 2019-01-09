package org.springmeetup.reactiveredis;

import java.time.Duration;

import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springmeetup.reactiveredis.model.TeamStanding;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ReactiveRestController {

	private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	
	private final ReactiveHashOperations<String, String ,Person> reactivePersonHashOperations;

	private final ReactiveZSetOperations<String, TeamStanding> reactiveTeamStandingZSetOperations;
	
	private final ReactiveHashOperations<String, String ,TeamStanding> reactiveTeamStandingHashOperations;
	
	@GetMapping(value = "/mono-demo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Mono<Person> monoDemo() {
		return Mono.just(new Person(5l));
	}
	
	@GetMapping(value = "/flux-demo", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public Flux<Person> fluxDemo() {
		return Flux.interval(Duration.ofMillis(100))
				.take(10)
				.map(t -> new Person(t));
	}
	
	@GetMapping(value = "/flux-demo/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
	public Flux<Person> fluxDemoStream() {
		return Flux.interval(Duration.ofSeconds(1l))
				.map(t -> new Person(t));
	}

	@GetMapping(value = "/mono-increment")
	public Mono<Long> monoIncrement() {
		return reactiveStringRedisTemplate.opsForValue().increment("counter");
	}

	@GetMapping(value = "/mono-decrement")
	public Mono<Long> monoDecrement() {
		return reactiveStringRedisTemplate.opsForValue().decrement("counter");
	}
	
	@GetMapping(value = "/flux-strings")
	public Flux<String> fluxStrings() {
		return reactiveStringRedisTemplate.opsForHash().values("STRINGS")
				.map(o -> (String) o);
	}
	
	@GetMapping(value = "/flux-from-redis")
	public Flux<Person> fluxFromRedis() {
		return reactivePersonHashOperations.values("person")
				.log();
	}
	
	@PostMapping("/team-standing/hashed")
	public Mono<Boolean> hashTeamStanding(@RequestBody TeamStanding teamStanding ) {
		return reactiveTeamStandingHashOperations.put("standings_hash", teamStanding.getTeamId(), teamStanding);
	}

	@GetMapping("/team-standing/hashed")
	public Flux<TeamStanding> getHashTeamStandings() {
		return reactiveTeamStandingHashOperations.values("standings_hash");
	}

	@GetMapping("/team-standing/hashed/{team_id}")
	public Mono<TeamStanding> getHashTeamStandings(@PathVariable("team_id") String teamId) {
		return reactiveTeamStandingHashOperations.get("standings_hash", teamId);
	}
	

	@PostMapping("/team-standing/sorted")
	public Mono<Boolean> sortNewTeamStanding(@RequestBody TeamStanding teamStanding ) {
		return reactiveTeamStandingZSetOperations.range("standings", Range.unbounded())
				.filter(teamStandingFromDb -> teamStandingFromDb.getTeamId().equals(teamStanding.getTeamId()))
				.next()
				.doOnNext(any -> System.out.println("found : " + any))
				.flatMap(teamStandingFromDb -> {
					return reactiveTeamStandingZSetOperations.remove("standings", teamStandingFromDb)
							.doOnNext(any -> System.out.println("removed: " + any))
							.then(reactiveTeamStandingZSetOperations.add("standings", teamStanding, Long.parseLong(teamStanding.getPosition())));
				})
				.switchIfEmpty(Mono.defer(() -> reactiveTeamStandingZSetOperations.add("standings", teamStanding, Long.parseLong(teamStanding.getPosition()))));
		
	}
	
	@GetMapping("/team-standing/sorted")
	public Flux<TeamStanding> getAllTeamStanding() {
		return reactiveTeamStandingZSetOperations.range("standings", Range.unbounded());
	}
}

