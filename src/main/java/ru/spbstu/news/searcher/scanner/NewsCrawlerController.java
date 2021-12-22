package ru.spbstu.news.searcher.scanner;

import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsCrawlerController {
    @NotNull
    private final NewsCrawlerFactory newsCrawlerFactory;

    @Autowired
    public NewsCrawlerController(@NotNull final NewsCrawlerFactory newsCrawlerFactory) {
        this.newsCrawlerFactory = newsCrawlerFactory;
    }

    public void launchCrawling() throws Exception {
        for (String resource : CrawlerConfig.resources) {
            CrawlConfig config = new CrawlConfig();

            configure(config);

            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            controller.addSeed(resource);

            controller.startNonBlocking(newsCrawlerFactory, 10);
        }
    }

    private void configure(@NotNull final CrawlConfig config) {
        // delay
        config.setPolitenessDelay(100);
        // cache folder
        config.setCrawlStorageFolder(Files.createTempDir().getAbsolutePath());
        // parsing depth
        config.setMaxDepthOfCrawling(1);
        // parsing count
        config.setMaxPagesToFetch(20);
        // resume after process death
        config.setResumableCrawling(true);
    }
}
