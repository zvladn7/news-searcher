package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.exceptions.JedisConnectionException;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

public class Cache implements Closeable {

    private static int CACHE_POOL_SIZE = 5;

    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    private final JedisPool jedisPool;

    public Cache(@NotNull String cacheHost,
                 @NotNull Integer cachePort) {
        Validate.notNull(cacheHost);
        Validate.notNull(cachePort);
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(CACHE_POOL_SIZE);
        this.jedisPool = new JedisPool(poolConfig, cacheHost, cachePort, 0);
    }
    
    public void put(@NotNull String query,
                    @NotNull Collection<SearchCacheItem> searchCacheItems,
                    @NotNull Long totalCount) {
        try {
            jedisPool.getResource().set(query, CacheItemSerializer.serialize(searchCacheItems, totalCount));
        } catch (JedisConnectionException ex) {
            logger.warn("Failed connection to cache(Redis DB) on put with query: [{}]", query, ex);
        }
    }

    @Nullable
    public Pair<Long, List<SearchCacheItem>> get(@NotNull String query) {
        String item = null;
        try {
            item = jedisPool.getResource().get(query);
        } catch (JedisConnectionException ex) {
            logger.warn("Failed connection to cache(Redis DB) on get with query: [{}]", query, ex);
        }
        if (item == null) {
            return null;
        }
        return CacheItemSerializer.deserialize(item);
    }

    public void invalidate(@NotNull SearchIndexDocument document) {
        try {
            jedisPool.getResource().flushDB();
        } catch (JedisConnectionException ex) {
            logger.warn("Failed connection to cache(Redis DB) on invalidate with document: [{}]", document, ex);
        }
    }

    @Override
    public void close() {
        jedisPool.close();
    }
}
