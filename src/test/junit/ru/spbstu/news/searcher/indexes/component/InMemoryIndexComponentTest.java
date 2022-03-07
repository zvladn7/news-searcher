package ru.spbstu.news.searcher.indexes.component;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryIndexComponentTest {

    @Mock
    private InMemoryIndexDirectoryRepository repository;

    private RAMDirectory ramDirectory;

    private InMemoryIndexComponent component;

    @Before
    public void setUp() {
        this.ramDirectory = new RAMDirectory();
        this.component = new InMemoryIndexComponent(repository);
    }

    @Test(expected = NullPointerException.class)
    public void construct_inMemoryIndexDirectoryRepositoryNull() {
        new InMemoryIndexComponent(null);
    }

    @Test
    public void index_DirectoryNull() {
        Mockito.doReturn(null).when(repository).provideRAMDirectory();
        Assert.assertNull(component.index(null));
    }

    @Test
    public void index_Normal() {
        List<SearchIndexDocument> indexDocuments = Collections.singletonList(new SearchIndexDocument(1L, "t1"));
        Mockito.doReturn(ramDirectory).when(repository).provideRAMDirectory();
        Assert.assertEquals(ramDirectory, component.index(indexDocuments));
    }

    @Test(expected = NullPointerException.class)
    public void deleteByDatabaseId_databaseIdNull() {
        component.deleteByDatabaseId(null, ramDirectory);
    }

    @Test(expected = NullPointerException.class)
    public void deleteByDatabaseId_DirecotryNull() {
        component.deleteByDatabaseId(123L, null);
    }

    @Test
    public void deleteByDatabaseId_Normal() {
        List<SearchIndexDocument> indexDocuments = Collections.singletonList(new SearchIndexDocument(1L, "t1"));
        component.deleteByDatabaseId(123L, ramDirectory);
        Mockito.verify(repository, Mockito.times(1))
                .releaseRAMDirectory(ramDirectory);
    }

}
