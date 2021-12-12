package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisConnectionException;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

public class Cache implements Closeable {

    private static final Logger logger = LoggerFactory.getLogger(Cache.class);

    private volatile Jedis jedis;
    private final String cacheHost;
    private final Integer cachePort;

    public Cache(@NotNull String cacheHost,
                 @NotNull Integer cachePort) {
        Validate.notNull(cacheHost);
        Validate.notNull(cachePort);
        this.jedis = new Jedis(cacheHost, cachePort);
        this.cacheHost = cacheHost;
        this.cachePort = cachePort;
    }
    
    public void put(@NotNull String query,
                    @NotNull Collection<SearchCacheItem> searchCacheItems,
                    @NotNull Long totalCount) {
        try {
            jedis.set(query, CacheItemSerializer.serialize(searchCacheItems, totalCount));
        } catch (JedisConnectionException ex) {
            logger.warn("Try to reload connection to cache(Redis DB) on put with query: [{}]", query, ex);
            this.jedis = new Jedis(cacheHost, cachePort);
            jedis.set(query, CacheItemSerializer.serialize(searchCacheItems, totalCount));
        }
    }

    @Nullable
    public Pair<Long, List<SearchCacheItem>> get(@NotNull String query) {
        String item;
        try {
            item = jedis.get(query);
        } catch (JedisConnectionException ex) {
            logger.warn("Try to reload connection to cache(Redis DB) on get with query: [{}]", query, ex);
            this.jedis = new Jedis(cacheHost, cachePort);
            item = jedis.get(query);
        }
        if (item == null) {
            return null;
        }
        return CacheItemSerializer.deserialize(item);
    }

    public void invalidate(@NotNull SearchIndexDocument document) {
        try {
            jedis.flushDB();
        } catch (JedisConnectionException ex) {
            logger.warn("Try to reload connection to cache(Redis DB) on invalidate with document: [{}]", document, ex);
            this.jedis = new Jedis(cacheHost, cachePort);
            jedis.flushDB();
        }
    }

    @Override
    public void close() {
        jedis.close();
    }
}
