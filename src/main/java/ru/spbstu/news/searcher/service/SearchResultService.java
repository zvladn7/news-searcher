package ru.spbstu.news.searcher.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.database.SearchResultRepository;

import java.util.Optional;

@Service
public class SearchResultService {

    private final SearchResultRepository searchResultRepository;

    @Autowired
    public SearchResultService(final SearchResultRepository searchResultRepository) {
        this.searchResultRepository = searchResultRepository;
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
}
