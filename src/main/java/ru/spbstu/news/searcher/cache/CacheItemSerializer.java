package ru.spbstu.news.searcher.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CacheItemSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheItemSerializer.class);

    public static final String DELIMITER = "~@~";
    public static final String TOTAL_COUNT_DELIMITER = "@@@";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(@NotNull Collection<SearchCacheItem> cacheItems, Long totalCount) {
        String cacheItemsString = cacheItems.stream()
                .map(next -> {
                    try {
                        return objectMapper.writeValueAsString(next);
                    } catch (JsonProcessingException e) {
                        logger.warn("Cannot serialize cache items", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.joining(DELIMITER));
        return String.join(TOTAL_COUNT_DELIMITER, String.valueOf(totalCount), cacheItemsString);
    }

    public static Pair<Long, List<SearchCacheItem>> deserialize(@NotNull String cacheItemsString) {
        String[] totalCountAndSearchItems = cacheItemsString.split(TOTAL_COUNT_DELIMITER);
        long totalCount = Long.parseLong(totalCountAndSearchItems[0]);
        String[] splitedItems = totalCountAndSearchItems[1].split(DELIMITER);
        List<SearchCacheItem> searchCacheItems = Arrays.stream(splitedItems)
                .map(item -> {
                    try {
                        return objectMapper.readValue(item, SearchCacheItem.class);
                    } catch (JsonProcessingException e) {
                        logger.warn("Cannot deserialize cache items", e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return Pair.create(totalCount, searchCacheItems);
    }

}
