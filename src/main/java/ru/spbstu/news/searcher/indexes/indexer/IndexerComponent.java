package ru.spbstu.news.searcher.indexes.indexer;

import org.apache.lucene.index.Term;
import org.jetbrains.annotations.NotNull;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import javax.naming.OperationNotSupportedException;
import java.io.IOException;

public interface IndexerComponent {

    void open(@NotNull String indexDir) throws LuceneIndexingException;

    void index(@NotNull SearchIndexDocument searchIndexDocument) throws LuceneOpenException;

    void delete(@NotNull Term term);

    void commit() throws IOException;

    void close();

    String dir() throws OperationNotSupportedException;

}
