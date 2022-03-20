package ru.spbstu.news.searcher.scanner;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CrawlerConfig {

    @Value("#{'${crawler.resources}'.split(',')}")
    private String[] resources;

    @Value("${crawler.politeness-delay}")
    private Integer delayPolicy;

    @Value("${crawler.max-pages-to-fetch}")
    private Integer maxPagesToFetch;

    @Value("${crawler.start-blocking}")
    private Boolean isBlocking;

    @Value("${crawler.thread-number}")
    private Integer numberOfCrawlers;

    public Integer getDelayPolicy() {
        return delayPolicy;
    }

    public Integer getNumberOfCrawlers() {
        return numberOfCrawlers;
    }

    public Boolean getBlocking() {
        return isBlocking;
    }

    public Integer getMaxPagesToFetch() {
        return maxPagesToFetch;
    }

    public List<String> getResources() {
        return List.of(resources);
    }
}
