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
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.indexes.AnalyzerProvider;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.SearchIndexDocumentConverter;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.io.IOException;

@Component
public class InMemoryIndexComponent {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryIndexComponent.class);

    private static final int DEFAULT_TOP_DOCS_COUNT = 1;

    private final Directory memoryIndex;
    private final Analyzer analyzer ;

    public InMemoryIndexComponent() {
        this.memoryIndex = new RAMDirectory();
        this.analyzer = AnalyzerProvider.provide();
    }

    public void index(@NotNull SearchIndexDocument searchIndexDocument) throws LuceneOpenException {
        try (IndexWriter writer = new IndexWriter(memoryIndex, new IndexWriterConfig(analyzer))) {
            Document document = SearchIndexDocumentConverter.convertInMemory(searchIndexDocument);
            writer.addDocument(document);
        } catch (IOException e) {
            logger.warn("Cannot index document [{}] in memory index", searchIndexDocument, e);
        }
    }

    public Document searchIndex(Query query) {
        try (IndexReader reader = DirectoryReader.open(memoryIndex)) {
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

    public void deleteByDatabaseId(@NotNull Long databaseId) {
        Term term = SearchIndexDocumentConverter.createDatabaseIdTerm(databaseId);
        try (IndexWriter writer = new IndexWriter(memoryIndex, new IndexWriterConfig(analyzer))) {
            writer.deleteDocuments(term);
        } catch (IOException e) {
            logger.warn("Cannot delete documents with database id [{}] in memory index", databaseId, e);
        }
    }

}
