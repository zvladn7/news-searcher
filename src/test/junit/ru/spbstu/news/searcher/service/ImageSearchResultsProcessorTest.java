package ru.spbstu.news.searcher.service;

import com.google.common.collect.ImmutableMap;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class ImageSearchResultsProcessorTest {

    private static final String QUERY = "футбол";
    private static final List<SearchResult> DATABASE_ENTITIES = Collections.emptyList();
    private static final Map<Long, SearchIndexDocument> DATABASE_IDS_TO_DOCUMENT = Collections.emptyMap();
    private static final long TOTAL_COUNT = 10L;

    @Mock
    private Cache cache;

    @Mock
    private TitleExtractor titleExtractor;

    private ImageSearchResultsProcessor imageSearchResultsProcessor;

    @Before
    public void setUp() {
        this.imageSearchResultsProcessor = new ImageSearchResultsProcessor(cache, titleExtractor);
    }

        @Test(expected = NullPointerException.class)
    public void getFindImageResult_QueryNull() {
        imageSearchResultsProcessor.getFindImageResult(null, DATABASE_ENTITIES, DATABASE_IDS_TO_DOCUMENT, TOTAL_COUNT);
    }

    @Test(expected = NullPointerException.class)
    public void getFindImageResult_DatabaseEntitiesNull() {
        imageSearchResultsProcessor.getFindImageResult(QUERY, null, DATABASE_IDS_TO_DOCUMENT, TOTAL_COUNT);
    }

    @Test(expected = NullPointerException.class)
    public void getFindImageResult_DatabaseIdsToDocumentNull() {
        imageSearchResultsProcessor.getFindImageResult(QUERY, DATABASE_ENTITIES, null, TOTAL_COUNT);
    }

    @Test(expected = NullPointerException.class)
    public void getFindImageResult_TotalCountNull() {
        imageSearchResultsProcessor.getFindImageResult(QUERY, DATABASE_ENTITIES, DATABASE_IDS_TO_DOCUMENT, null);
    }

    @Test
    public void getFindImageResult_Normal() {
         long databaseId1 = 1L;
        String fullText1 = "Футбол - это круто!";
        String url1 = "url1";
        String imageUrl1 = "imageUrl1";
        long databaseId2 = 2L;
        String fullText2 = "Футбол - это не круто!";
        String url2 = "url2";
        String imageUrl2 = "imageUrl2";
        List<SearchResult> databaseEntities = List.of(
                new SearchResult(databaseId1, url1, List.of(imageUrl1)),
                new SearchResult(databaseId2, url2, List.of(imageUrl2))
        );
        Map<Long, SearchIndexDocument> databaseIdsToDocument = ImmutableMap.of(
                databaseId1, new SearchIndexDocument(databaseId1, fullText1),
                databaseId2, new SearchIndexDocument(databaseId2, fullText2)
        );
        Mockito.doReturn(fullText1).when(titleExtractor).getTitleFromFullText(fullText1, databaseId1, QUERY);
        Mockito.doReturn(fullText2).when(titleExtractor).getTitleFromFullText(fullText2, databaseId2, QUERY);
        Mockito.doNothing().when(cache).put(Mockito.any(), Mockito.any(), Mockito.any());
        FindImageResult findImageResult = imageSearchResultsProcessor.getFindImageResult(QUERY, databaseEntities, databaseIdsToDocument, TOTAL_COUNT);
        Assert.assertEquals(2, findImageResult.getTotalCount());
        List<ImageItem> searchItemsResult = findImageResult.getImageItems();
        Assert.assertEquals(databaseEntities.size(), searchItemsResult.size());
        List<ImageItem> searchItemsExpected = List.of(
                new ImageItem(databaseId1,  imageUrl1, fullText1, url1),
                new ImageItem(databaseId2, imageUrl2, fullText2, url2)
        );
        for (ImageItem imageItem : searchItemsResult) {
            Assert.assertTrue(searchItemsExpected.contains(imageItem));
        }
        Mockito.verify(cache, Mockito.times(1))
                .put(Mockito.any(), Mockito.any(), Mockito.any());
    }

}
