package ru.spbstu.news.searcher.cache;

import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import redis.clients.jedis.Jedis;

import java.io.Closeable;
import java.util.Collection;
import java.util.List;

public class Cache implements Closeable {

    private final Jedis jedis;

    public Cache(@NotNull Jedis jedis) {
        this.jedis = jedis;
    }
    
    public void put(@NotNull String query,
                    @NotNull Collection<SearchCacheItem> searchCacheItems,
                    @NotNull Long totalCount) {
        jedis.set(query, CacheItemSerializer.serialize(searchCacheItems, totalCount));
    }

    @Nullable
    public Pair<Long, List<SearchCacheItem>> get(@NotNull String query) {
        String item = jedis.get(query);
        if (item == null) {
            return null;
        }
        return CacheItemSerializer.deserialize(jedis.get(query));
    }

    @Override
    public void close() {
        jedis.close();
    }
}
