package ru.spbstu.news.searcher.indexes.component;

import org.apache.lucene.store.RAMDirectory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.mockito.Mockito.spy;

@RunWith(MockitoJUnitRunner.class)
public class InMemoryIndexDirectoryRepositoryTest {

    private static final int DIRECTORIES_AMOUNT = 10;

    @Mock
    private RAMDirectory ramDirectory;

    @Test(expected = NullPointerException.class)
    public void construct_NPE() {
        new InMemoryIndexDirectoryRepository(null);
    }

    @Test
    public void construct_Normal() {
        InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository
                = new InMemoryIndexDirectoryRepository(DIRECTORIES_AMOUNT);
        Assert.assertSame(DIRECTORIES_AMOUNT, inMemoryIndexDirectoryRepository.getDirectoriesMap().size());
        inMemoryIndexDirectoryRepository.getDirectoriesMap()
                .forEach((k, v) -> Assert.assertFalse(v));
        Assert.assertNotNull(inMemoryIndexDirectoryRepository.getLock());
        Assert.assertEquals(DIRECTORIES_AMOUNT, inMemoryIndexDirectoryRepository.getSemaphore().availablePermits());
    }

    @Test
    public void provideRAMDirectory_Null() {
        InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository
                = new InMemoryIndexDirectoryRepository(1);
        inMemoryIndexDirectoryRepository.provideRAMDirectory();
        Assert.assertNull(inMemoryIndexDirectoryRepository.provideRAMDirectory());
    }

    @Test
    public void provideRAMDirectory_Normal() {
        InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository
                = new InMemoryIndexDirectoryRepository(1);
        RAMDirectory directory = inMemoryIndexDirectoryRepository.provideRAMDirectory();
        Assert.assertNotNull(directory);
        Assert.assertTrue(inMemoryIndexDirectoryRepository.getDirectoriesMap().get(directory));
    }

    @Test
    public void provideRAMDirectory_InterruptedException() throws InterruptedException {
        InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository
                = new InMemoryIndexDirectoryRepository(1);
        inMemoryIndexDirectoryRepository.setSemaphore(spy(inMemoryIndexDirectoryRepository.getSemaphore()));
        Mockito.doThrow(InterruptedException.class).when(inMemoryIndexDirectoryRepository.getSemaphore()).tryAcquire(3, TimeUnit.SECONDS);
        Assert.assertNull(inMemoryIndexDirectoryRepository.provideRAMDirectory());
    }

    @Test
    public void releaseRAMDirectory_Normal() {
        InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository
                = new InMemoryIndexDirectoryRepository(DIRECTORIES_AMOUNT);
        inMemoryIndexDirectoryRepository.releaseRAMDirectory(ramDirectory);
        inMemoryIndexDirectoryRepository.setSemaphore(spy(inMemoryIndexDirectoryRepository.getSemaphore()));
        inMemoryIndexDirectoryRepository.releaseRAMDirectory(ramDirectory);
        Mockito.verify(inMemoryIndexDirectoryRepository.getSemaphore(), Mockito.times(1))
                .release();
        Assert.assertFalse(inMemoryIndexDirectoryRepository.getDirectoriesMap().get(ramDirectory));
    }

}
