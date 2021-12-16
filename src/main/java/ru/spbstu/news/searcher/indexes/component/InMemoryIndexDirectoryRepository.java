package ru.spbstu.news.searcher.indexes.component;

import org.apache.commons.lang3.Validate;
import org.apache.lucene.store.RAMDirectory;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

@Component
public class InMemoryIndexDirectoryRepository {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryIndexDirectoryRepository.class);

    private Map<RAMDirectory, Boolean> directoriesMap;
    private Semaphore semaphore;

    public InMemoryIndexDirectoryRepository(@Value("${indexer.memory-directory-amount}") Integer directoriesAmount) {
        Validate.notNull(directoriesAmount);
        initDirectoriesMap(directoriesAmount);
        this.semaphore = new Semaphore(directoriesAmount);
    }

    public void initDirectoriesMap(Integer directoriesAmount) {
        this.directoriesMap = new HashMap<>(directoriesAmount);
        for (int i = 0; i < directoriesAmount; ++i) {
            directoriesMap.put(new RAMDirectory(), false);
        }
    }

    @Nullable
    public RAMDirectory provideRAMDirectory() {
        try {
            boolean acquired = semaphore.tryAcquire(3, TimeUnit.SECONDS);
            if (acquired) {
                RAMDirectory directory = directoriesMap.entrySet()
                        .stream()
                        .filter(e -> !e.getValue())
                        .map(Map.Entry::getKey)
                        .findFirst()
                        .orElse(null);
                directoriesMap.put(directory, true);
                return directory;
            } else {
                logger.warn("Cannot get RAM directory cause all of it is already acquired");
                return null;
            }
        } catch (InterruptedException e) {
            logger.error("Cannot provide RAM directory to index current title", e);
            return null;
        }
    }

    public void releaseRAMDirectory(RAMDirectory ramDirectory) {
        directoriesMap.put(ramDirectory, false);
        semaphore.release();
    }

}
