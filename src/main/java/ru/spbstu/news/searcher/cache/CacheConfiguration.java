package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class CacheConfiguration {

    @Value("${cache.redis.host}")
    private String cacheHost;

    @Value("${cache.redis.port}")
    private Integer cachePort;

    @Bean
    public Jedis jedis() {
        Validate.notNull(cacheHost);
        Validate.notNull(cachePort);
        return new Jedis(cacheHost, cachePort);
    }

    @Bean
    public Cache cache(@NotNull Jedis jedis) {
        Validate.notNull(jedis);
        return new Cache(jedis);
    }

}
