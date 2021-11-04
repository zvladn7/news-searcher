package ru.spbstu.news.searcher.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class CacheItemSerializer {

    private static final Logger logger = LoggerFactory.getLogger(CacheItemSerializer.class);

    private static final String DELIMITER = "~@~";
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static String serialize(@NotNull List<SearchCacheItem> cacheItems) {
        return cacheItems.stream()
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
    }

    public static List<SearchCacheItem> deserialize(@NotNull String cacheItemsString) {
        String[] splitedItems = cacheItemsString.split(DELIMITER);
        return Arrays.stream(splitedItems)
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
    }

}
