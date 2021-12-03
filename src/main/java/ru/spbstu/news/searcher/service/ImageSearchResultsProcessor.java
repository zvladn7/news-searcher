package ru.spbstu.news.searcher.service;

import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.cache.SearchCacheItem;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class ImageSearchResultsProcessor implements SearchResultsProcessor<FindImageResult> {

    private final Cache cache;

    @Autowired
    public ImageSearchResultsProcessor(@NotNull Cache cache) {
        this.cache = cache;
    }

    @Override
    public FindImageResult getFindImageResult(@NotNull String query,
                                              @NotNull List<SearchResult> databaseEntities,
                                              @NotNull Map<Long, SearchIndexDocument> databaseIdsToDocument,
                                              @NotNull Long totalCount) {
        List<ImageItem> imageItems = new ArrayList<>();
        Set<SearchCacheItem> searchCacheItems = new HashSet<>();
        for (SearchResult searchResult : databaseEntities) {
            SearchIndexDocument searchIndexDocument = databaseIdsToDocument.get(searchResult.getId());
            String fullText = searchIndexDocument.getFullText();
            String title = fullText.substring(0, SearchResultService.DEFAULT_TITLE_LENGTH);
            List<String> imageUrls = searchResult.getImageUrls();
            if (CollectionUtils.isNotEmpty(imageItems)) {
                for (String imageUrl : imageUrls) {
                    imageItems.add(new ImageItem(
                            searchResult.getId(),
                            imageUrl,
                            title,
                            searchResult.getUrl()
                    ));
                }
            }
            searchCacheItems.add(new SearchCacheItem(
                    searchResult.getId(),
                    title,
                    searchResult.getUrl(),
                    searchResult.getImageUrls()));
        }
        cache.put(query, new ArrayList<>(searchCacheItems), totalCount);
        return new FindImageResult(
                imageItems,
                totalCount
        );
    }

}
