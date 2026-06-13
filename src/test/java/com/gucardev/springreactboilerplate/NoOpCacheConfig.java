package com.gucardev.springreactboilerplate;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;

import static org.mockito.Mockito.*;

/**
 * Provides a no-op {@link RedisConnectionFactory} so the production
 * {@code RedisCacheConfig} can construct its cache managers without a real
 * Redis instance. Every cache operation silently misses (returns null) and
 * write/evict operations are no-ops.
 *
 * <p>The connection is created with {@link org.mockito.Mockito#RETURNS_DEEP_STUBS}
 * so any Redis command — including ones the app hasn't used yet (hashes, sets,
 * pub/sub, etc.) — returns a sensible default automatically. No need to update
 * this file when new cache operations are added.
 */
@TestConfiguration
public class NoOpCacheConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisConnection conn = mock(RedisConnection.class, RETURNS_DEEP_STUBS);
        when(factory.getConnection()).thenReturn(conn);
        return factory;
    }
}
