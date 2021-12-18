package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfiguration {

    @Value("${cache.redis.host}")
    private String cacheHost;

    @Value("${cache.redis.port}")
    private Integer cachePort;

    @Bean
    public Cache cache() {
        Validate.notNull(cacheHost);
        Validate.notNull(cachePort);
        return new Cache(cacheHost, cachePort);
    }

}
