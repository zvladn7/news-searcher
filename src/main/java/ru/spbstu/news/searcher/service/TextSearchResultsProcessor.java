package ru.spbstu.news.searcher.service;

import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.cache.SearchCacheItem;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.SearchItem;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class TextSearchResultsProcessor implements SearchResultsProcessor<FindByTextResult> {

    private final Cache cache;

    @Autowired
    public TextSearchResultsProcessor(@NotNull Cache cache) {
        this.cache = cache;
    }

    @Override
    public FindByTextResult getFindImageResult(@NotNull String query, @NotNull List<SearchResult> databaseEntities, @NotNull Map<Long, SearchIndexDocument> databaseIdsToDocument, @NotNull Long totalCount) {
        Map<SearchItem, SearchCacheItem> collectMap = databaseEntities.stream()
                    .map(entity -> {
                        SearchIndexDocument searchIndexDocument = databaseIdsToDocument.get(entity.getId());
                        String fullText = searchIndexDocument.getFullText();
                        int indexOfQuery = fullText.indexOf(query);
                        String title = fullText.substring(indexOfQuery, indexOfQuery + SearchResultService.DEFAULT_TITLE_LENGTH);
                        return Pair.create(
                                new SearchItem(entity.getId(), entity.getUrl(), title),
                                new SearchCacheItem(entity.getId(), title, entity.getUrl(), entity.getImageUrls()));
                    }).collect(Collectors.toMap(Pair::getKey, Pair::getValue));
        cache.put(query, collectMap.values(), totalCount);
        return new FindByTextResult(
                new ArrayList<>(collectMap.keySet()),
                totalCount);
    }
}
