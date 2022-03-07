package ru.spbstu.news.searcher.indexes.indexer;


import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

@RunWith(MockitoJUnitRunner.class)
public class CacheInvalidatorTest {

    private static final long DATABASE_ID = 123L;
    private static final String FULL_TEXT = "full_text";

    @Mock
    private Cache cache;

    private CacheInvalidator cacheInvalidator;

    @Before
    public void setUp() {
        this.cacheInvalidator = new CacheInvalidator(cache);
    }

    @Test(expected = NullPointerException.class)
    public void invalidate_SearchIndexDocumentNull() {
        cacheInvalidator.invalidate(null);
    }

    @Test
    public void invalidate_Normal() {
        Mockito.doNothing().when(cache).invalidate(Mockito.any());
        Assert.assertTrue(cacheInvalidator.invalidate(new SearchIndexDocument(DATABASE_ID, FULL_TEXT)));
    }

    @Test
    public void invalidate_Failed() {
        Mockito.doThrow(RuntimeException.class).when(cache).invalidate(Mockito.any());
        Assert.assertFalse(cacheInvalidator.invalidate(new SearchIndexDocument(DATABASE_ID, FULL_TEXT)));
    }

}
