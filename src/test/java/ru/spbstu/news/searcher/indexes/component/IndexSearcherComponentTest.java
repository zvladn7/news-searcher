package ru.spbstu.news.searcher.indexes.component;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.math3.util.Pair;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexIllegalPartitions;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.util.List;

import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class IndexSearcherComponentTest {

    @Mock
    private IndexWriterComponent indexWriterComponent;

    @Test(expected = LuceneIndexIllegalPartitions.class)
    public void construct_InvalidPartitions() throws LuceneIndexIllegalPartitions {
        new IndexSearcherComponent(0, "/src", indexWriterComponent);
    }

    @Test(expected = NullPointerException.class)
    public void construct_NullIndexWriterComponent() throws LuceneIndexIllegalPartitions {
        new IndexSearcherComponent(0, "/src", null);
    }

    @Test
    public void construct_Normal() throws LuceneIndexingException {
        int partitions = 1;
        String indexDir = "/indexDir";
        IndexSearcherComponent indexSearcherComponent = new IndexSearcherComponent(partitions, indexDir, indexWriterComponent);
        indexSearcherComponent.getLuceneIndexSearchers()[0] = spy(indexSearcherComponent.getLuceneIndexSearchers()[0]);
        Mockito.doNothing().when(indexSearcherComponent.getLuceneIndexSearchers()[0]).open(indexDir);
        indexSearcherComponent.init();
        Assert.assertEquals(partitions, indexSearcherComponent.getLuceneIndexSearchers().length);
        Mockito.verify(indexWriterComponent, Mockito.times(1))
                .setOnIndexUpdateListener(Mockito.any());
        Mockito.verify(indexSearcherComponent.getLuceneIndexSearchers()[0], Mockito.times(1))
                .open(indexDir);
    }

    @Test(expected = LuceneOpenException.class)
    public void construct_CannotOpenDirectoryForSearching() throws LuceneIndexingException {
        int partitions = 1;
        String indexDir = "/indexDir";
        IndexSearcherComponent indexSearcherComponent = new IndexSearcherComponent(partitions, indexDir, indexWriterComponent);
        indexSearcherComponent.init();
    }

    @Test
    public void toIndex_Normal() {
        int partitionIndex = 1;
        int partition = 2;
        Assert.assertEquals(partitionIndex, IndexSearcherComponent.toIndex(partition));
    }

    @Test
    public void searchIndexDocuments_ParseException() throws LuceneIndexIllegalPartitions {
        String textQuery = "футбол";
        int docsCount = 5;
        IndexSearcherComponent indexSearcherComponent = new IndexSearcherComponent(1, "/src", indexWriterComponent);
        try (MockedStatic<SearchIndexDocumentConverter> converter = Mockito.mockStatic(SearchIndexDocumentConverter.class)) {
            converter.when(() -> SearchIndexDocumentConverter.createQueryFullText(textQuery)).thenThrow(ParseException.class);
            Pair<List<SearchIndexDocument>, Long> results = indexSearcherComponent.searchIndexDocuments(textQuery, docsCount);
            Assert.assertTrue(CollectionUtils.isEmpty(results.getFirst()));
            Long expected = 0L;
            Assert.assertEquals(expected, results.getSecond());
        }
    }

    @Test
    public void searchIndexDocuments_TextQueryNull() throws LuceneIndexIllegalPartitions {
        String textQuery = null;
        int docsCount = 5;
        IndexSearcherComponent indexSearcherComponent = new IndexSearcherComponent(1, "/src", indexWriterComponent);
        Pair<List<SearchIndexDocument>, Long> results = indexSearcherComponent.searchIndexDocuments(textQuery, docsCount);
        Assert.assertTrue(CollectionUtils.isEmpty(results.getFirst()));
        Long expected = 0L;
        Assert.assertEquals(expected, results.getSecond());
    }

    @Test
    public void searchIndexDocuments_LuceneOpenException() throws LuceneIndexIllegalPartitions, LuceneOpenException {
        String textQuery = "футбол";
        int docsCount = 5;
        IndexSearcherComponent indexSearcherComponent = spy(new IndexSearcherComponent(1, "/src", indexWriterComponent));
        Mockito.doThrow(LuceneOpenException.class).when(indexSearcherComponent).search(Mockito.any(), Mockito.any(), Mockito.any());
        Pair<List<SearchIndexDocument>, Long> results = indexSearcherComponent.searchIndexDocuments(textQuery, docsCount);
        Assert.assertTrue(CollectionUtils.isEmpty(results.getFirst()));
        Long expected = 0L;
        Assert.assertEquals(expected, results.getSecond());
    }
}
