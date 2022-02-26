package ru.spbstu.news.searcher.cache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class CacheItemSerializerTest {

    private static final long TOTAL_COUNT = 10L;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test(expected = NullPointerException.class)
    public void serialize_NPE() {
        CacheItemSerializer.serialize(null, TOTAL_COUNT);
    }

    @Test
    public void serialize_NormalWork() throws JsonProcessingException {
        List<SearchCacheItem> cacheItems = List.of(
                new SearchCacheItem(1L, "item1", "url1", null),
                new SearchCacheItem(2L, "item2", "url2", List.of("imageUrl1"))
        );
        String serialized = CacheItemSerializer.serialize(cacheItems, TOTAL_COUNT);
        String[] split = serialized.split(CacheItemSerializer.TOTAL_COUNT_DELIMITER);
        Assert.assertEquals(TOTAL_COUNT, Long.parseLong(split[0]));
        String itemsInJson = split[1];
        String[] itemsSplit = itemsInJson.split(CacheItemSerializer.DELIMITER);
        Assert.assertEquals(cacheItems.size(), itemsSplit.length);
        for (int i = 0; i < cacheItems.size(); ++i) {
            String itemInJson = itemsSplit[i];
            SearchCacheItem searchCacheItem = objectMapper.readValue(itemInJson, SearchCacheItem.class);
            Assert.assertEquals(cacheItems.get(i), searchCacheItem);
        }
    }

    @Test
    public void serialize_NullFiltration() {
        SearchCacheItem searchCacheItem = new SearchCacheItem(1L, "item1", "url1", null);
        List<SearchCacheItem> cacheItems = new ArrayList<>();
        cacheItems.add(null);
        cacheItems.add(searchCacheItem);
        cacheItems.add(null);
        String serializedData = CacheItemSerializer.serialize(cacheItems, TOTAL_COUNT);
        Pair<Long, List<SearchCacheItem>> deserializedData = CacheItemSerializer.deserialize(serializedData);
        List<SearchCacheItem> deserializedDataList = deserializedData.getValue();
        Assert.assertEquals(1, deserializedDataList.size());
        Assert.assertEquals(searchCacheItem, deserializedDataList.get(0));
    }

    @Test(expected = NullPointerException.class)
    public void deserialize_NPE() {
        CacheItemSerializer.deserialize(null);
    }

    @Test
    public void deserialize_NormalWork() {
        List<SearchCacheItem> cacheItems = List.of(
                new SearchCacheItem(1L, "item1", "url1", null),
                new SearchCacheItem(2L, "item2", "url2", List.of("imageUrl1"))
        );
        String serialized = CacheItemSerializer.serialize(cacheItems, TOTAL_COUNT);
        Pair<Long, List<SearchCacheItem>> deserialize = CacheItemSerializer.deserialize(serialized);
        List<SearchCacheItem> deserializedList = deserialize.getValue();
        Assert.assertEquals(cacheItems.size(), deserializedList.size());
        //todo: use iterable matcher here
        for (SearchCacheItem item : deserializedList) {
            Assert.assertTrue(cacheItems.contains(item));
        }
    }

    @Test
    public void deserialize_NullFiltration() {
        List<SearchCacheItem> cacheItems = List.of(
                new SearchCacheItem(1L, "item1", "url1", null),
                new SearchCacheItem(2L, "item2", "url2", List.of("imageUrl1"))
        );
        String serialized = CacheItemSerializer.serialize(cacheItems, TOTAL_COUNT);
        serialized = serialized.substring(0, serialized.length() - 1) + "hello".hashCode();
        Pair<Long, List<SearchCacheItem>> deserialize = CacheItemSerializer.deserialize(serialized);
        List<SearchCacheItem> deserializedList = deserialize.getValue();
        Assert.assertEquals(1, deserializedList.size());
        for (SearchCacheItem item : deserializedList) {
            Assert.assertTrue(cacheItems.contains(item));
        }
    }

}
