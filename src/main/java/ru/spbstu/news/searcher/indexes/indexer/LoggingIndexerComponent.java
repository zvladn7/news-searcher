package ru.spbstu.news.searcher.indexes.indexer;

import org.apache.lucene.index.Term;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public class LoggingIndexerComponent implements IndexerComponent {

    private static final Logger logger = LoggerFactory.getLogger(LoggingIndexerComponent.class);

    private final IndexerComponent indexerComponent;
    private final boolean loggingEnabled;

    /**
     * Decorator for IndexerComponent to log if the action was happened.
     * @param indexerComponent - real indexer
     * @param loggingEnabled - flag which used to indicate whether we need to log it or not
     */
    public LoggingIndexerComponent(IndexerComponent indexerComponent,
                                   boolean loggingEnabled) {
        this.indexerComponent = indexerComponent;
        this.loggingEnabled = loggingEnabled;
    }

    @Override
    public void open(@NotNull String indexDir) throws LuceneIndexingException {
        indexerComponent.open(indexDir);
        if (loggingEnabled) {
            logger.info("Index which stored in directory:[{}] is opened", indexDir);
        }
    }

    @Override
    public void index(@NotNull SearchIndexDocument searchIndexDocument) throws LuceneOpenException {
        indexerComponent.index(searchIndexDocument);
        if (loggingEnabled) {
            logger.info("Document: [{}] is stored in index in file: [{}]", searchIndexDocument, dir());
        }
    }

    @Override
    public void delete(@NotNull Term term) {
        indexerComponent.delete(term);
        if (loggingEnabled) {
            logger.info("Documents with term: [{}] is deleted in index file: [{}]", term, dir());
        }
    }

    @Override
    public void commit() throws IOException {
        indexerComponent.commit();
        if (loggingEnabled) {
            logger.info("Data is committed in index file: [{}]", dir());
        }
    }

    @Override
    public void close() {
        indexerComponent.close();
        if (loggingEnabled) {
            logger.info("Index in file:[{}] is closed", dir());
        }
    }

    @Override
    public String dir() {
        try {
            return indexerComponent.dir();
        } catch (OperationNotSupportedException e) {
            logger.warn("Cannot get dir", e);
            return null;
        }
    }
}
