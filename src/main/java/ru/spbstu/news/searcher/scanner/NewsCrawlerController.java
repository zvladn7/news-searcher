package ru.spbstu.news.searcher.scanner;

import com.google.common.io.Files;
import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;
import org.jetbrains.annotations.NotNull;
import org.jsoup.helper.Validate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NewsCrawlerController {
    @NotNull
    private final NewsCrawlerFactory newsCrawlerFactory;

    @Autowired
    public NewsCrawlerController(@NotNull final NewsCrawlerFactory newsCrawlerFactory) {
        Validate.notNull(newsCrawlerFactory);
        this.newsCrawlerFactory = newsCrawlerFactory;
    }

    public void launchCrawling() throws Exception {
        for (String resource : newsCrawlerFactory.crawlerConfig.getResources()) {
            CrawlConfig config = new CrawlConfig();

            configure(config);

            PageFetcher pageFetcher = new PageFetcher(config);
            RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
            RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
            CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);

            controller.addSeed(resource);

            if (newsCrawlerFactory.crawlerConfig.getBlocking()) {
                controller.start(newsCrawlerFactory, newsCrawlerFactory.crawlerConfig.getNumberOfCrawlers());
            } else {
                controller.startNonBlocking(newsCrawlerFactory, newsCrawlerFactory.crawlerConfig.getNumberOfCrawlers());
            }

        }
    }

    public void configure(@NotNull final CrawlConfig config) {
        // delay
        config.setPolitenessDelay(newsCrawlerFactory.crawlerConfig.getDelayPolicy());
        // cache folder
        config.setCrawlStorageFolder(Files.createTempDir().getAbsolutePath());
        // parsing depth
//        config.setMaxDepthOfCrawling(1);
        // parsing count
        config.setMaxPagesToFetch(newsCrawlerFactory.crawlerConfig.getMaxPagesToFetch());
        // resume after process death
        config.setResumableCrawling(true);
    }
}
