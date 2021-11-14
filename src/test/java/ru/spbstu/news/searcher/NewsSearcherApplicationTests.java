package ru.spbstu.news.searcher;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.util.List;

import static org.junit.Assert.assertTrue;

@SpringBootTest
class NewsSearcherApplicationTests {

    @Autowired
    private SearchResultService searchResultService;

    @Test
    void contextLoads() {
    }

    @Test
    public void testAdding() {
        final SearchResult newSearchResult = new SearchResult("url", List.of("imageUrl"));
        searchResultService.createSearchResult(newSearchResult);
        final SearchResult searchResult = searchResultService.getSearchResultItemById(newSearchResult.getId()).get();
        assertTrue(newSearchResult.equals(searchResult));
        searchResultService.removeSearchResult(newSearchResult);
    }

}
