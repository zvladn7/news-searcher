package ru.spbstu.news.searcher.cache;

import org.jetbrains.annotations.NotNull;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.util.List;

public class Cache implements Closeable {

    private final Jedis jedis;

    public Cache(@NotNull Jedis jedis) {
        this.jedis = jedis;
    }
    
    public void put(@NotNull String query,
                    @NotNull List<SearchCacheItem> searchCacheItems) {
        jedis.set(query, CacheItemSerializer.serialize(searchCacheItems));
    }

    public List<SearchCacheItem> get(@NotNull String query) {
        return CacheItemSerializer.deserialize(jedis.get(query));
    }

    @Override
    public void close() {
        jedis.close();
    }
}
