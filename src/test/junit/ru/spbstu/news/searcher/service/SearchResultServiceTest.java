package ru.spbstu.news.searcher.service;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.database.SearchResultRepository;
import ru.spbstu.news.searcher.indexes.component.IndexSearcherComponent;
import ru.spbstu.news.searcher.indexes.component.IndexWriterComponent;

@RunWith(MockitoJUnitRunner.class)
public class SearchResultServiceTest {

    @Mock
    private SearchResultRepository searchResultRepository;
    @Mock
    private IndexSearcherComponent indexSearcherComponent;
    @Mock
    private IndexWriterComponent indexWriterComponent;
    @Mock
    private Cache cache;
    @Mock
    private ImageSearchResultsProcessor imageSearchResultsProcessor;
    @Mock
    private TextSearchResultsProcessor textSearchResultsProcessor;

    private SearchResultService service;

    @Before
    public void setUp() {
        this.service = new SearchResultService(searchResultRepository, indexSearcherComponent, indexWriterComponent,
                cache, imageSearchResultsProcessor, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_searchResultRepositoryNull() {
        new SearchResultService(null, indexSearcherComponent, indexWriterComponent,
                cache, imageSearchResultsProcessor, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_indexSearcherComponentNull() {
        new SearchResultService(searchResultRepository, null, indexWriterComponent,
                cache, imageSearchResultsProcessor, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_indexWriterComponentNull() {
        new SearchResultService(searchResultRepository, indexSearcherComponent, null,
                cache, imageSearchResultsProcessor, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_cacheNull() {
        new SearchResultService(searchResultRepository, indexSearcherComponent, indexWriterComponent,
                null, imageSearchResultsProcessor, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_imageSearchResultsProcessorNull() {
        new SearchResultService(searchResultRepository, indexSearcherComponent, indexWriterComponent,
                cache, null, textSearchResultsProcessor);
    }

    @Test(expected = NullPointerException.class)
    public void construct_textSearchResultsProcessorNull() {
        new SearchResultService(searchResultRepository, indexSearcherComponent, indexWriterComponent,
                cache, imageSearchResultsProcessor, null);
    }

    @Test
    public void getEndIndexForImage_FirstPage() {
        Assert.assertSame(SearchResultService.FIRST_IMAGE_PAGE_SIZE, service.getEndIndexForImage(1, 0));
    }

    @Test
    public void getEndIndexForImage_AnotherPage() {
        int start = 10;
        Assert.assertSame(SearchResultService.DEFAULT_IMAGE_PAGE_SIZE + start, service.getEndIndexForImage(2, start));
    }

    @Test
    public void getStartIndexForImage_FirstPage() {
        Assert.assertSame(0, service.getStartIndexForImage(1));
    }

    @Test
    public void getStartIndexForImage_AnotherPage() {
        int start = 10;
        Assert.assertSame(SearchResultService.FIRST_IMAGE_PAGE_SIZE, service.getStartIndexForImage(2));
    }

}
