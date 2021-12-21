package ru.spbstu.news.searcher.indexes.component;

import org.apache.lucene.index.Term;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.indexes.IndexPartitioner;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexIllegalPartitions;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.indexes.indexer.CacheInvalidator;
import ru.spbstu.news.searcher.indexes.indexer.IndexerComponent;
import ru.spbstu.news.searcher.indexes.indexer.LoggingIndexerComponent;
import ru.spbstu.news.searcher.indexes.indexer.LuceneIndexWriter;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.naming.OperationNotSupportedException;
import java.io.IOException;

@Component
public class IndexWriterComponent implements IndexerComponent {

    private final String indexDir;
    private final int partitions;
    private final IndexerComponent[] luceneIndexWriters;
    private final CacheInvalidator cacheInvalidator;
    private Runnable onIndexUpdateListener;

    public IndexWriterComponent(@Value("${indexer.partions.amount}") int partitions,
                                @Value("${indexer.trace.actions}") boolean loggingEnabled,
                                @Value("${indexer.indexDir}") String indexDir,
                                @NotNull CacheInvalidator cacheInvalidator) throws LuceneIndexIllegalPartitions {
        this.cacheInvalidator = cacheInvalidator;
        if (partitions <= 0) {
            throw new LuceneIndexIllegalPartitions("Number of partitions is less than 0");
        }
        this.luceneIndexWriters = new IndexerComponent[partitions];
        for (int partition = 1; partition <= partitions; ++partition) {
            luceneIndexWriters[toIndex(partition)] = new LoggingIndexerComponent(new LuceneIndexWriter(String.valueOf(partition), partition), loggingEnabled);
        }
        this.indexDir = indexDir;
        this.partitions = partitions;
    }

    public static int toIndex(int partition) {
        return partition - 1;
    }

    public void setOnIndexUpdateListener(Runnable onIndexUpdateListener) {
        this.onIndexUpdateListener = onIndexUpdateListener;
    }

    @PostConstruct
    public void init() throws LuceneIndexingException, IOException {
        open(indexDir);
    }

    @Override
    public void open(@NotNull String indexDir) throws LuceneIndexingException, IOException {
        for (IndexerComponent luceneIndexWriter : luceneIndexWriters) {
            luceneIndexWriter.open(indexDir);
            luceneIndexWriter.commit();
        }
    }

    @Override
    public void index(@NotNull SearchIndexDocument searchIndexDocument) throws LuceneOpenException {
        int partition = IndexPartitioner.getPartition(searchIndexDocument, partitions);
        IndexerComponent luceneIndexWriter = luceneIndexWriters[toIndex(partition)];
        luceneIndexWriter.index(searchIndexDocument);
        cacheInvalidator.invalidate(searchIndexDocument);
        onIndexUpdateListener.run();
    }

    @Override
    public void delete(@NotNull Term term) {
        for (IndexerComponent luceneIndexWriter : luceneIndexWriters) {
            luceneIndexWriter.delete(term);
        }
    }

    @Override
    public void commit() throws IOException {
        for (IndexerComponent luceneIndexWriter : luceneIndexWriters) {
            luceneIndexWriter.commit();
        }
    }

    @PreDestroy
    @Override
    public void close() {
        for (IndexerComponent luceneIndexWriter : luceneIndexWriters) {
            luceneIndexWriter.close();
        }
    }

    @Override
    public String dir() throws OperationNotSupportedException {
        throw new OperationNotSupportedException("Operation not provided for collection of indexer writers");
    }
}
