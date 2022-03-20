package ru.spbstu.news.searcher.cache;

import org.apache.commons.lang3.Validate;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
        JedisPoolConfig config = new JedisPoolConfig ();
        config.setMaxTotal (200);
        config.setMaxIdle (50);
        config.setMinIdle (8); // Устанавливаем минимальное количество простоя
        config.setMaxWaitMillis (10000);
        config.setTestOnBorrow(true);
        config .setTestOnReturn(true); // Подключаем сканирование при простое
        config.setTestWhileIdle (true); // Указывает количество миллисекунд для перехода в спящий режим между сканированием незанятого объекта проверкой config.setTimeBetweenEvictionRunsMillis (30000); // Указывает на незанятое обнаружение объекта каждые Максимум количество просканированных объектов config.setNumTestsPerEvictionRun (10); // Указывает, что объект остается в состоянии ожидания, по крайней мере, самое короткое время, прежде чем он может быть отсканирован и удален с помощью объекта, проверяющего неактивный объект; этот элемент имеет смысл, только когда timeBetweenEvictionRunsMillis больше чем 0
        config.setMinEvictableIdleTimeMillis(60000);
        this.jedisPool = new JedisPool (config, cacheHost, cachePort, 10000);
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

    public void invalidate(SearchIndexDocument document) {
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
