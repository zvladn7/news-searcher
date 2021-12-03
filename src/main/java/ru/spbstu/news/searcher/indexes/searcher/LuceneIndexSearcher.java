package ru.spbstu.news.searcher.indexes.searcher;

import org.apache.commons.math3.util.Pair;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LuceneIndexSearcher implements Searcher {

    private static final int DEFAULT_TOP_DOCS_COUNT = 10;

    private static final Logger logger = LoggerFactory.getLogger(LuceneIndexSearcher.class);

    private final String indexName;
    private final int partition;

    private File indexFile;
    private Directory indexDirectory;

    private IndexReader indexReader;
    private IndexSearcher indexSearcher;

    public LuceneIndexSearcher(String indexName, int partition) {
        this.indexName = indexName;
        this.partition = partition;
    }

    public boolean isOpen() {
        return indexDirectory != null;
    }

    protected void ensureOpen() throws LuceneOpenException {
        if (!isOpen()) {
            throw new LuceneOpenException("Index is not opened!");
        }
    }

    public void open(@NotNull String indexDir) throws LuceneIndexingException {
        if (isOpen()) {
            logger.warn("The index file with name: [{}] is already opened", indexFile.getName());
            throw new LuceneIndexingException("The index file is already opened!");
        }
         try {
            this.indexFile = new File(indexDir + File.separator + partition);
            this.indexDirectory = FSDirectory.open(this.indexFile.toPath());
        } catch (IOException e) {
            throw new LuceneOpenException("Cannot open lucene file to read, name: " + (indexDir + File.separator + partition), e);
        }

        try {
            this.indexReader = DirectoryReader.open(this.indexDirectory);
            this.indexSearcher = new IndexSearcher(indexReader);
        } catch (IOException e) {
            throw new LuceneOpenException("Cannot create IndexReader with indexDir=" + indexDir, e);
        }
    }

    public Pair<List<SearchIndexDocument>, Long> search(@NotNull Query query,
                                                        @NotNull Sort sort,
                                                        @Nullable Integer docsCount) throws LuceneOpenException {
        ensureOpen();
        try {
            docsCount = docsCount == null ? DEFAULT_TOP_DOCS_COUNT : docsCount;
            TopDocs topDocs = indexSearcher.search(query, docsCount, sort);
            List<SearchIndexDocument> documents = new ArrayList<>();
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                Document document = indexSearcher.doc(scoreDoc.doc);
                SearchIndexDocument searchIndexDocument = SearchIndexDocumentConverter.convert(document);
                documents.add(searchIndexDocument);
            }
            return Pair.create(documents, topDocs.totalHits.value);
        } catch (IOException e) {
            logger.warn("Cannot get documents with query: [{}]", query, e);
            return Pair.create(Collections.emptyList(), 0L);
        }
    }

    public void close() {
        String name = indexFile.getName();
        if (!isOpen()) {
            logger.info("The index file with name: [{}] is already opened", name);
            return;
        }
        try {
            indexDirectory.close();
            indexDirectory = null;
            indexReader.close();
            indexReader = null;
        } catch (IOException e) {
            logger.warn("Cannot close index file: [{}]", name, e);
        }
    }


}
