package ru.spbstu.news.searcher.indexes.indexer;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NIOFSDirectory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.news.searcher.indexes.AnalyzerProvider;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.io.File;
import java.io.IOException;

public class LuceneIndexWriter implements IndexerComponent {

    private static final Logger logger = LoggerFactory.getLogger(LuceneIndexWriter.class);

    private final String indexName;
    private final int partition;

    private File indexFile;
    private Directory indexDirectory;

    private IndexWriter indexWriter;

    public LuceneIndexWriter(String indexName, int partition) {
        this.indexName = indexName;
        this.partition = partition;
    }

    @Override
    public void open(@NotNull String indexDir) throws LuceneIndexingException {
        if (isOpen()) {
            logger.warn("The index file with name: [{}] is already opened", indexFile.getName());
            throw new LuceneIndexingException("The index file is already opened!");
        }

        try {
            this.indexFile = new File(indexDir + File.separator + partition);
            this.indexDirectory = NIOFSDirectory.open(this.indexFile.toPath());
        } catch (IOException e) {
            throw new LuceneOpenException("Cannot open lucene file with name: " + (indexDir + File.separator + partition), e);
        }

        try {
            IndexWriterConfig indexWriterConfig = new IndexWriterConfig(AnalyzerProvider.provide());
            this.indexWriter = new IndexWriter(indexDirectory, indexWriterConfig);
        } catch (IOException e) {
            throw new LuceneOpenException("Cannot create IndexWriter with indexDir={}" + indexDir, e);
        }
    }

    public boolean isOpen() {
        return indexDirectory != null;
    }

    protected void ensureOpen() throws LuceneOpenException {
        if (!isOpen()) {
            throw new LuceneOpenException("Index is not opened!");
        }
    }

    @Override
    public void index(@NotNull SearchIndexDocument searchIndexDocument) throws LuceneOpenException {
        ensureOpen();
        try {
            Document document = SearchIndexDocumentConverter.convert(searchIndexDocument);
            indexWriter.addDocument(document);
        } catch (IOException e) {
            logger.warn("Cannot add document to index", e);
        } finally {
            try {
                indexWriter.commit();
            } catch (IOException e) {
                logger.warn("Cannot commit when index", e);
            }
        }
    }

    @Override
    public void delete(@NotNull Term term) {
        try {
            indexWriter.deleteDocuments(term);
        } catch (IOException e) {
            logger.warn("Cannot delete document to index", e);
        }
    }

    @Override
    public void commit() throws IOException {
        try {
            indexWriter.commit();
        } catch (IOException e) {
            logger.warn("Cannot commit index", e);
        }
    }

    @Override
    public void close() {
        String name = indexFile.getName();
        if (!isOpen()) {
            logger.info("The index file with name: [{}] is already opened", name);
            return;
        }
        try {
            commit();
            indexDirectory.close();
            indexDirectory = null;
            indexWriter.close();
            indexWriter = null;
        } catch (IOException e) {
            logger.warn("Cannot close index file: [{}]", name, e);
        }
    }

    @Override
    public String dir() {
        return indexDirectory != null ? indexFile.getName() : null;
    }

}
