package ru.spbstu.news.searcher.indexes.indexer;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.io.IOException;

import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class LuceneIndexWriterTest {

    private static final String INDEX_DIR = "/index123";
    private static final String INDEX_NAME = "index_name";
    private static final int PARTITION = 1;

    @Mock
    private IndexWriter indexWriter;
    @Mock
    private Term term;

    private LuceneIndexWriter writer;

    @Before
    public void setUp() {
        this.writer = spy(new LuceneIndexWriter(INDEX_NAME, PARTITION));
    }

    @Test(expected = NullPointerException.class)
    public void open_IndexDirNull() throws LuceneIndexingException {
        writer.open(null);
    }

    @Test(expected = LuceneOpenException.class)
    public void open_CannotOpenDirectory() throws LuceneIndexingException {
        writer.open(INDEX_DIR);
    }

    @Test
    public void isOpen_False() {
        Assert.assertFalse(writer.isOpen());
    }

    @Test(expected = LuceneOpenException.class)
    public void ensureOpen_Exception() throws LuceneOpenException {
        writer.ensureOpen();
    }

    @Test
    public void commit_Normal() throws IOException {
        writer.setIndexWriter(indexWriter);
        Mockito.doReturn(1L).when(indexWriter).commit();
        Assert.assertTrue(writer.commit());
        Mockito.verify(indexWriter, Mockito.times(1))
                .commit();
    }

    @Test(expected = LuceneOpenException.class)
    public void index_notOpen() throws LuceneOpenException {
        SearchIndexDocument indexDocument = new SearchIndexDocument(123L, "text");
        writer.index(indexDocument);
    }

    @Test
    public void index_NormalAdd() throws IOException, LuceneOpenException {
        SearchIndexDocument indexDocument = new SearchIndexDocument(123L, "text");
        writer.setIndexWriter(indexWriter);
        Mockito.doReturn(true).when(writer).isOpen();
        Mockito.doReturn(1L).when(indexWriter).addDocument(Mockito.any());
        writer.index(indexDocument);
        Mockito.verify(indexWriter, Mockito.times(1))
                .commit();
    }

    @Test
    public void commit_CannotCommit() throws IOException {
        writer.setIndexWriter(indexWriter);
        Mockito.doThrow(IOException.class).when(indexWriter).commit();
        boolean commit = writer.commit();
        Assert.assertFalse(commit);
        Mockito.verify(indexWriter, Mockito.times(1))
                .commit();
    }

    @Test
    public void delete_Normal() throws IOException {
        writer.setIndexWriter(indexWriter);
        Mockito.doReturn(1L).when(indexWriter).deleteDocuments(term);
        writer.delete(term);
        Mockito.verify(indexWriter, Mockito.times(1))
                .deleteDocuments(term);
    }

}
