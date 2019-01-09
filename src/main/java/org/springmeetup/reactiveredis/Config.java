package org.springmeetup.reactiveredis;

import static org.springframework.data.redis.serializer.RedisSerializationContext.newSerializationContext;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.data.redis.core.ReactiveZSetOperations;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springmeetup.reactiveredis.model.TeamStanding;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class Config {

	@Bean
	public ReactiveStringRedisTemplate reactiveStringRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		return new ReactiveStringRedisTemplate(reactiveRedisConnectionFactory);
	}
	
	@Bean
	public ReactiveRedisTemplate<String, Person> reactivePersonRedisTemplate(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializationContext<String, Person> serializationContext = RedisSerializationContext.<String, Person>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(configureJackson2JsonRedisSerializer(Person.class))
				.build();
		
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}

	@Bean	
    public ReactiveHashOperations<String, String ,Person> reactivePersonHashOperations(ReactiveRedisTemplate<String, Person> reactivePersonRedisTemplate) {
    	return reactivePersonRedisTemplate.<String, Person>opsForHash();
    }
	
	@Bean
	public ReactiveRedisTemplate<String, Person> reactivePersonRedisTemplateForHash(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializationContext<String, Person> serializationContext = RedisSerializationContext.<String, Person>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(configureJackson2JsonRedisSerializer(Person.class))
				.build();
		
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}
	
	@Bean
	public ReactiveRedisTemplate<String, TeamStanding> reactiveTeamStandingRedisTemplateForHash(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
		RedisSerializationContext<String, TeamStanding> serializationContext = RedisSerializationContext.<String, TeamStanding>newSerializationContext(new StringRedisSerializer())
				.hashKey(new StringRedisSerializer())
				.hashValue(configureJackson2JsonRedisSerializer(TeamStanding.class))
				.build();
		
        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, serializationContext);
	}
	
	@Bean	
    public ReactiveHashOperations<String, String ,TeamStanding> reactiveTeamStandingHashOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    	return reactiveTeamStandingRedisTemplateForHash(reactiveRedisConnectionFactory).<String, TeamStanding>opsForHash();
    }
    
	
    @Bean
    public ReactiveRedisTemplate<String, TeamStanding> reactiveTeamStandingRedisTemplateForSort(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {

        Jackson2JsonRedisSerializer<TeamStanding> serializer = configureJackson2JsonRedisSerializer(TeamStanding.class);

        RedisSerializationContext.RedisSerializationContextBuilder<String, TeamStanding> builder =
                newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, TeamStanding> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(reactiveRedisConnectionFactory, context);

    }
	
	@Bean	
    public ReactiveZSetOperations<String, TeamStanding> reactiveTeamStandingZSetOperations(ReactiveRedisConnectionFactory reactiveRedisConnectionFactory) {
    	return reactiveTeamStandingRedisTemplateForSort(reactiveRedisConnectionFactory).<String, TeamStanding>opsForZSet();
    }
	
	public <T> Jackson2JsonRedisSerializer<T> configureJackson2JsonRedisSerializer(Class<T> t) {
		ObjectMapper objectMapper = new ObjectMapper()
				.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Jackson2JsonRedisSerializer<T> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(t);
		jackson2JsonRedisSerializer.setObjectMapper(objectMapper);
		
		return jackson2JsonRedisSerializer;
	}
	
}
