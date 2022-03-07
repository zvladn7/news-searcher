package ru.spbstu.news.searcher.service;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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
    private final TitleExtractor titleExtractor;

    @Autowired
    public ImageSearchResultsProcessor(@NotNull Cache cache,
                                       @NotNull TitleExtractor titleExtractor) {
        this.cache = cache;
        this.titleExtractor = titleExtractor;
    }

    @Override
    public FindImageResult getFindImageResult(@NotNull String query,
                                              @NotNull List<SearchResult> databaseEntities,
                                              @NotNull Map<Long, SearchIndexDocument> databaseIdsToDocument,
                                              @NotNull Long totalCount) {
        Validate.notNull(query);
        Validate.notNull(databaseEntities);
        Validate.notNull(databaseIdsToDocument);
        Validate.notNull(totalCount);
        List<ImageItem> imageItems = new ArrayList<>();
        Set<SearchCacheItem> searchCacheItems = new HashSet<>();
        for (SearchResult searchResult : databaseEntities) {
            SearchIndexDocument searchIndexDocument = databaseIdsToDocument.get(searchResult.getId());
            String fullText = searchIndexDocument.getFullText();
            Long databaseId = searchIndexDocument.getDatabaseId();
            String title = titleExtractor.getTitleFromFullText(fullText, databaseId, query);
            List<String> imageUrls = searchResult.getImageUrls();
            if (CollectionUtils.isNotEmpty(imageUrls)) {
                for (String imageUrl : imageUrls) {
                    if (StringUtils.isNotBlank(imageUrl)) {
                        imageItems.add(new ImageItem(
                                searchResult.getId(),
                                imageUrl,
                                title,
                                searchResult.getUrl()
                        ));
                    }
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
                imageItems.size()
        );
    }

}
