package ru.spbstu.news.searcher.indexes.searcher;

import org.apache.commons.math3.util.Pair;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneIndexingException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;

import java.util.List;

public interface Searcher {

    void open(@NotNull String indexDir) throws LuceneIndexingException;

    Pair<List<SearchIndexDocument>, Long> search(@NotNull Query query, @NotNull Sort sort, @Nullable Integer docsCount) throws LuceneOpenException;

    void close();

}
