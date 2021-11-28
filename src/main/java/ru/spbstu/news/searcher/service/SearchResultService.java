package ru.spbstu.news.searcher.service;

import org.apache.commons.collections.CollectionUtils;

import org.apache.commons.math3.util.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spbstu.news.searcher.cache.Cache;
import ru.spbstu.news.searcher.cache.SearchCacheItem;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.controller.result.SearchItem;
import ru.spbstu.news.searcher.controller.result.SimilarItem;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.database.SearchResultRepository;
import ru.spbstu.news.searcher.exception.ResultNotFoundException;
import ru.spbstu.news.searcher.indexes.SearchIndexDocument;
import ru.spbstu.news.searcher.indexes.component.IndexSearcherComponent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class SearchResultService {

    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int DEFAULT_IMAGE_PAGE_SIZE = 20;
    public static final int DEFAULT_TITLE_LENGTH = 100;
    public static final int SIMILAR_ITEMS_COUNT = 8;

    private final SearchResultRepository searchResultRepository;
    private final IndexSearcherComponent indexSearcherComponent;
    private final Cache cache;
    private final ImageSearchResultsProcessor imageSearchResultsProcessor;
    private final TextSearchResultsProcessor textSearchResultsProcessor;

    @Autowired
    public SearchResultService(@NotNull final SearchResultRepository searchResultRepository,
                               @NotNull final IndexSearcherComponent indexSearcherComponent,
                               @NotNull final Cache cache,
                               @NotNull final ImageSearchResultsProcessor imageSearchResultsProcessor,
                               @NotNull final TextSearchResultsProcessor textSearchResultsProcessor) {
        this.searchResultRepository = searchResultRepository;
        this.indexSearcherComponent = indexSearcherComponent;
        this.cache = cache;
        this.imageSearchResultsProcessor = imageSearchResultsProcessor;
        this.textSearchResultsProcessor = textSearchResultsProcessor;
    }

    public void createSearchResult(final SearchResult searchResult) {
        searchResultRepository.save(searchResult);
    }

    public Optional<SearchResult> getSearchResultItemById(final Long id) {
        return searchResultRepository.findById(id);
    }

    public void removeSearchResult(final SearchResult searchResult) {
        searchResultRepository.delete(searchResult);
    }

    @NotNull
    public FindByTextResult findByText(@NotNull String textQuery,
                                       @NotNull Integer page) throws ResultNotFoundException {
        return findByText(textQuery, page, DEFAULT_PAGE_SIZE);
    }

    @NotNull
    public FindByTextResult findByText(@NotNull String textQuery,
                                       @NotNull Integer page,
                                       @Nullable Integer pageSize) throws ResultNotFoundException {
        pageSize = pageSize != null ? pageSize : DEFAULT_PAGE_SIZE;
        int searchMaxThreshold = page * pageSize;
        Pair<Long, List<SearchCacheItem>> cacheItemsPair = cache.get(textQuery);
        if (cacheItemsPair != null) {
            Long cacheTotalCount = cacheItemsPair.getKey();
            List<SearchCacheItem> cacheItems = cacheItemsPair.getValue();
            int size = cacheItems.size();
            if (CollectionUtils.isNotEmpty(cacheItems) && searchMaxThreshold - pageSize <= size) {
                return extractFromCache(cacheItems, page, pageSize, cacheTotalCount);
            }
        }
        FindByTextResult resultsFromIndex = getResults(textQuery, searchMaxThreshold, textSearchResultsProcessor);
        if (resultsFromIndex != null) {
            List<SearchItem> searchItems = resultsFromIndex.getSearchItems();
            int startIndex = (page - 1) * (pageSize);
            int endIndex = startIndex + page;
            return new FindByTextResult(
                    searchItems.subList(startIndex, endIndex),
                    resultsFromIndex.getTotalCount());
        }
        throw new ResultNotFoundException("No results for query: " + textQuery);
    }

    @Nullable
    public <T> T getResults(@NotNull String query,
                            int searchMaxThreshold,
                            SearchResultsProcessor<T> searchResultsProcessor) {
        Pair<List<SearchIndexDocument>, Long> searchIndexDocumentsResult = indexSearcherComponent.searchIndexDocuments(query, searchMaxThreshold);
        Long totalCount = searchIndexDocumentsResult.getValue();
        Map<Long, SearchIndexDocument> databaseIdsToDocument = searchIndexDocumentsResult.getKey()
                .stream()
                .collect(Collectors.toMap(SearchIndexDocument::getDatabaseId, Function.identity()));
        List<SearchResult> databaseEntities = searchResultRepository.findAllById(databaseIdsToDocument.keySet());
        if (CollectionUtils.isNotEmpty(databaseEntities)) {
            return searchResultsProcessor.getFindImageResult(query, databaseEntities, databaseIdsToDocument, totalCount);
        }
        return null;
    }

    private FindByTextResult extractFromCache(@NotNull List<SearchCacheItem> cacheItems,
                                              @NotNull Integer page,
                                              @NotNull Integer pageSize,
                                              @NotNull Long cacheTotalCount) {
        int startIndex = (page - 1) * (pageSize);
        int endIndex = startIndex + pageSize;
        List<SearchItem> searchItems = cacheItems.subList(startIndex, endIndex)
                .stream()
                .map(item -> new SearchItem(item.getId(), item.getTitle(), item.getUrl()))
                .collect(Collectors.toList());
        return new FindByTextResult(searchItems, cacheTotalCount);
    }

    public List<SimilarItem> findSimilar(String query) throws ResultNotFoundException {
        FindByTextResult textResult = findByText(query, 1, SIMILAR_ITEMS_COUNT);
        List<SearchItem> searchItems = textResult.getSearchItems();
        return searchItems.stream()
                .map(item -> new SimilarItem(item.getTitle(), item.getLink()))
                .collect(Collectors.toList());
    }

    public FindImageResult findImages(int page, String query) throws ResultNotFoundException {
        int searchMaxThreshold = page * DEFAULT_IMAGE_PAGE_SIZE;
        Pair<Long, List<SearchCacheItem>> cacheItemsPair = cache.get(query);
        if (cacheItemsPair != null) {
            Long cacheTotalCount = cacheItemsPair.getKey();
            List<SearchCacheItem> cacheItems = cacheItemsPair.getValue();
            int size = cacheItems.size();
            if (CollectionUtils.isNotEmpty(cacheItems) && searchMaxThreshold - DEFAULT_IMAGE_PAGE_SIZE <= size) {
                return extractImagesResultFromCache(cacheItems, page, DEFAULT_IMAGE_PAGE_SIZE, cacheTotalCount);
            }
        }
        FindImageResult resultsFromIndex = getResults(query, searchMaxThreshold, imageSearchResultsProcessor);
        if (resultsFromIndex != null) {
            List<ImageItem> searchItems = resultsFromIndex.getImageItems();
            int startIndex = (page - 1) * (DEFAULT_IMAGE_PAGE_SIZE);
            int endIndex = startIndex + page;
            return new FindImageResult(
                    searchItems.subList(startIndex, endIndex),
                    resultsFromIndex.getTotalCount());
        }
        throw new ResultNotFoundException("No results for query: " + query);
    }


    private FindImageResult extractImagesResultFromCache(List<SearchCacheItem> cacheItems,
                                                         int page,
                                                         int pageSize,
                                                         Long cacheTotalCount) {
        int startIndex = (page - 1) * (pageSize);
        int endIndex = startIndex + pageSize;
        cacheItems = cacheItems.subList(startIndex, endIndex);
        List<ImageItem> imageItems = new ArrayList<>();
        for (SearchCacheItem cacheItem : cacheItems) {
            List<String> imageUrls = cacheItem.getImageUrls();
            for (String imageUrl : imageUrls) {
                imageItems.add(new ImageItem(
                        cacheItem.getId(),
                        imageUrl,
                        cacheItem.getTitle(),
                        cacheItem.getUrl()
                ));
            }
        }
        return new FindImageResult(imageItems, cacheTotalCount);
    }

}