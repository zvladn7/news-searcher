package ru.spbstu.news.searcher.scanner;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import ru.spbstu.news.searcher.service.SearchResultService;

@Component
public class NewsCrawlerFactory implements CrawlController.WebCrawlerFactory<NewsCrawler> {
    @NotNull
    private final SearchResultService searchResultService;

    public NewsCrawlerFactory(@NotNull final SearchResultService searchResultService) {
        this.searchResultService = searchResultService;
    }

    @Override
    public NewsCrawler newInstance() throws Exception {
        return new NewsCrawler(searchResultService);
    }
}
