package ru.spbstu.news.searcher.indexes.component;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.indexes.AnalyzerProvider;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class InMemoryIndexComponent {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryIndexComponent.class);

    private static final int DEFAULT_TOP_DOCS_COUNT = 1;

    private final Analyzer analyzer ;
    private final InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository;

    public InMemoryIndexComponent(InMemoryIndexDirectoryRepository inMemoryIndexDirectoryRepository) {
        this.analyzer = AnalyzerProvider.provide();
        this.inMemoryIndexDirectoryRepository = inMemoryIndexDirectoryRepository;
    }

    @Nullable
    public RAMDirectory index(List<SearchIndexDocument> documentsToStoreInMemoryIndex) {
        RAMDirectory directory = inMemoryIndexDirectoryRepository.provideRAMDirectory();
        if (directory == null) {
            logger.warn("RAM directory is null");
            return null;
        }
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            for (SearchIndexDocument searchIndexDocument : documentsToStoreInMemoryIndex) {
                Document document = SearchIndexDocumentConverter.convertInMemory(searchIndexDocument);
                writer.addDocument(document);
            }
        } catch (LockObtainFailedException ex) {
            logger.warn("Lock failed",ex);
        } catch (IOException e) {
            logger.warn("Cannot index documents [{}] in memory index",
                    Arrays.toString(documentsToStoreInMemoryIndex.toArray()), e);
        }
        return directory;
    }

    public Document searchIndex(@NotNull Query query,
                                @NotNull RAMDirectory directory) {
        try (IndexReader reader = DirectoryReader.open(directory)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            TopDocs topDocs = searcher.search(query, DEFAULT_TOP_DOCS_COUNT); // TODO: probably we can create different method to search more or less docs
            for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
                return searcher.doc(scoreDoc.doc);
            }
        } catch (IOException e) {
            logger.warn("Cannot search query [{}] in memory index", query, e);
        }
        return null;
    }

    public void deleteByDatabaseId(@NotNull Long databaseId,
                                   @NotNull RAMDirectory directory) {
        Term term = SearchIndexDocumentConverter.createDatabaseIdTerm(databaseId);
        try (IndexWriter writer = new IndexWriter(directory, new IndexWriterConfig(analyzer))) {
            writer.deleteDocuments(term);
        } catch (IOException e) {
            logger.warn("Cannot delete documents with database id [{}] in memory index", databaseId, e);
        } finally {
            inMemoryIndexDirectoryRepository.releaseRAMDirectory(directory);
        }
    }

}
