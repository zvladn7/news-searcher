package ru.spbstu.news.searcher.service;

import org.jetbrains.annotations.NotNull;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.util.List;
import java.util.Map;

public interface SearchResultsProcessor<T> {

    T getFindImageResult(@NotNull String query,
                                       @NotNull List<SearchResult> databaseEntities,
                                       @NotNull Map<Long, SearchIndexDocument> databaseIdsToDocument,
                                       @NotNull Long totalCount);

}
