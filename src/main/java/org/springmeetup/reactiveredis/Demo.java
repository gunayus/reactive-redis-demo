package org.springmeetup.reactiveredis;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class Demo implements CommandLineRunner {

	private final ReactiveRedisConnectionFactory reactiveRedisConnectionFactory;
		
	private final ReactiveStringRedisTemplate reactiveStringRedisTemplate;
	
	private final ReactiveRedisTemplate<String, Person> reactivePersonRedisTemplate;
	
	private final ReactiveHashOperations<String, String ,Person> reactivePersonHashOperations;
	
	@Override
	public void run(String... args) throws Exception {
		// TODO Auto-generated method stub
		
		reactiveStringRedisTemplate.opsForHash()
			.put("STRINGS", "id-1", "value-1-x")
			.log()
			.subscribe(result -> System.out.println("string operation result : " + result));
		
		reactivePersonHashOperations.put("person", "id-1", new Person(1l))
			.log()
			.subscribe(result -> System.out.println("person operation result : " + result));
		
		reactivePersonHashOperations.put("person", "id-2", new Person(2l))
			.log()
			.subscribe(result -> System.out.println("person operation result : " + result));
	}
}
