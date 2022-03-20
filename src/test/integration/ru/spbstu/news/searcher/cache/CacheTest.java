package ru.spbstu.news.searcher.cache;

import org.apache.commons.math3.util.Pair;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.spbstu.news.searcher.service.TitleExtractor;
import ru.spbstu.news.searcher.util.SearcherTest;

import java.util.List;

public class CacheTest extends SearcherTest {

    @Autowired
    private Cache cache;
    @Autowired
    private TitleExtractor titleExtractor;

    @Test
    public void saveResultsToCacheTest() throws Exception {
        Long expectedCacheTotalCount = 1L;
        int expectedSearchCacheItemListSize = 1;
        int expectedImageUrls = 1;
        storeTestData();
        doRequestText();
        Pair<Long, List<SearchCacheItem>> cacheResult = cache.get(QUERY);
        Assert.assertNotNull(cacheResult);
        Assert.assertEquals(expectedCacheTotalCount, cacheResult.getKey());
        List<SearchCacheItem> searchCacheItemList = cacheResult.getValue();
        Assert.assertEquals(expectedSearchCacheItemListSize, searchCacheItemList.size());
        SearchCacheItem searchCacheItem = searchCacheItemList.get(0);
        String expectedTitle = titleExtractor.getTitleFromFullText(TEXT, searchCacheItem.getId(), QUERY);
        Assert.assertEquals(expectedTitle, searchCacheItem.getTitle());
        List<String> imageUrls = searchCacheItem.getImageUrls();
        Assert.assertNotNull(imageUrls);
        Assert.assertEquals(expectedImageUrls, imageUrls.size());
        Assert.assertEquals(IMAGE_URLS.get(0), imageUrls.get(0));
    }

}
