package ru.spbstu.news.searcher.cache;

import org.apache.commons.math3.util.Pair;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spbstu.news.searcher.scanner.NewsCrawlerController;
import ru.spbstu.news.searcher.service.TitleExtractor;
import ru.spbstu.news.searcher.util.SearcherTest;

import java.io.File;
import java.util.List;

@SpringBootTest(properties = { "indexer.indexDir=./indexText/indexCacheTest" })
public class CacheTest extends SearcherTest {

    @Autowired
    private Cache cache;
    @Autowired
    private TitleExtractor titleExtractor;
    @Autowired
    private NewsCrawlerController newsCrawlerController;

    @Before
    public void setUp() throws Exception {
        super.setUp();
        cache.invalidate(null);
    }

    @Test
    public void saveResultsToCacheTest_GetResult() throws Exception {
        Long expectedCacheTotalCount = 1L;
        int expectedSearchCacheItemListSize = 1;
        int expectedImageUrls = 1;
        storeTestData();
        Assert.assertNull(cache.get(QUERY));
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

    @Test
    public void resetCacheAfterUpdatingIndex() throws Exception {
        storeTestData();
        doRequestImages();
        Assert.assertNotNull(cache.get(QUERY));
        newsCrawlerController.launchCrawling();
        Assert.assertNull(cache.get(QUERY));
    }

    @AfterClass
    public static void close() {
        deleteDirectory(new File("./indexText/indexCacheTest"));
    }

}
