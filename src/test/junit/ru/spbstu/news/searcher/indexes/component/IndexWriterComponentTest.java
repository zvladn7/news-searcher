package ru.spbstu.news.searcher.indexes.component;

import org.apache.lucene.index.Term;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexIllegalPartitions;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.indexes.indexer.CacheInvalidator;

import javax.naming.OperationNotSupportedException;

import java.io.IOException;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class IndexWriterComponentTest {

    private static final int PARTITIONS = 1;
    private static final boolean LOGGING_ENABLED = true;
    private static final String INDEX_DIR = "./index";

    @Mock
    private CacheInvalidator cacheInvalidator;
    @Mock
    private Runnable runnable;
    @Mock
    private SearchIndexDocument searchIndexDocument;
    @Mock
    private Term term;

    @Test(expected = LuceneIndexIllegalPartitions.class)
    public void construct_InvalidPartitions() throws LuceneIndexIllegalPartitions {
        new IndexWriterComponent(-1, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
    }

    @Test(expected = NullPointerException.class)
    public void construct_IndexDirNull() throws LuceneIndexIllegalPartitions {
        new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, null, cacheInvalidator);
    }

    @Test(expected = NullPointerException.class)
    public void construct_CacheInvalidatorNull() throws LuceneIndexIllegalPartitions {
        new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, null);
    }

    @Test
    public void construct_Normal() throws LuceneIndexingException, IOException {
        int partitions = 1;
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(partitions, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.getLuceneIndexWriters()[0] = spy(indexWriterComponent.getLuceneIndexWriters()[0]);
        Mockito.doNothing().when(indexWriterComponent.getLuceneIndexWriters()[0]).open(INDEX_DIR);
        Mockito.doReturn(true).when(indexWriterComponent.getLuceneIndexWriters()[0]).commit();
        indexWriterComponent.init();
        Assert.assertEquals(partitions, indexWriterComponent.getLuceneIndexWriters().length);
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .open(INDEX_DIR);
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .commit();
    }

    @Test
    public void commit_Normal() throws LuceneIndexIllegalPartitions, IOException {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.getLuceneIndexWriters()[0] = spy(indexWriterComponent.getLuceneIndexWriters()[0]);
        Mockito.doReturn(true).when(indexWriterComponent.getLuceneIndexWriters()[0]).commit();
        indexWriterComponent.commit();
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .commit();
    }

    @Test
    public void delete_Normal() throws LuceneIndexIllegalPartitions {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.getLuceneIndexWriters()[0] = spy(indexWriterComponent.getLuceneIndexWriters()[0]);
        Mockito.doNothing().when(indexWriterComponent.getLuceneIndexWriters()[0]).delete(term);
        indexWriterComponent.delete(term);
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .delete(term);
    }

    @Test
    public void close_Normal() throws LuceneIndexIllegalPartitions {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.getLuceneIndexWriters()[0] = spy(indexWriterComponent.getLuceneIndexWriters()[0]);
        Mockito.doNothing().when(indexWriterComponent.getLuceneIndexWriters()[0]).close();
        indexWriterComponent.close();
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .close();
    }

    @Test
    public void setOnIndexUpdateListener_Normal() throws LuceneIndexIllegalPartitions {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.setOnIndexUpdateListener(runnable);
        Assert.assertNotNull(indexWriterComponent.getOnIndexUpdateListener());
        Assert.assertEquals(runnable, indexWriterComponent.getOnIndexUpdateListener());
    }

    @Test(expected = NullPointerException.class)
    public void index_NPE() throws LuceneIndexIllegalPartitions, LuceneOpenException {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        indexWriterComponent.index(null);
    }

    @Test
    public void index_Normal() throws LuceneIndexIllegalPartitions, LuceneOpenException {
        IndexWriterComponent indexWriterComponent = new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator);
        doReturn("full_text").when(searchIndexDocument).getFullText();
        indexWriterComponent.setOnIndexUpdateListener(runnable);
        indexWriterComponent.getLuceneIndexWriters()[0] = spy(indexWriterComponent.getLuceneIndexWriters()[0]);
        doReturn(true).when(cacheInvalidator).invalidate(searchIndexDocument);
        doNothing().when(indexWriterComponent.getLuceneIndexWriters()[0]).index(searchIndexDocument);
        doNothing().when(runnable).run();
        indexWriterComponent.index(searchIndexDocument);
        Mockito.verify(indexWriterComponent.getLuceneIndexWriters()[0], Mockito.times(1))
                .index(searchIndexDocument);
        Mockito.verify(cacheInvalidator, Mockito.times(1))
                .invalidate(searchIndexDocument);
        Mockito.verify(runnable, Mockito.times(1))
                .run();
    }

    @Test
    public void toIndex_Normal() {
        int partition = 4;
        int indexForPartition = 3;
        Assert.assertSame(indexForPartition, IndexWriterComponent.toIndex(partition));
    }

    @Test(expected = OperationNotSupportedException.class)
    public void dir_NotSupported() throws LuceneIndexIllegalPartitions, OperationNotSupportedException {
        new IndexWriterComponent(PARTITIONS, LOGGING_ENABLED, INDEX_DIR, cacheInvalidator).dir();
    }

}
