package ru.spbstu.news.searcher.scanner;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import ru.spbstu.news.searcher.service.SearchResultService;

@RunWith(MockitoJUnitRunner.class)
public class NewsCrawlerFactoryTest {

    @Mock
    private SearchResultService searchResultService;
    @Mock
    private CrawlerConfig crawlerConfig;

    @Test(expected = IllegalArgumentException.class)
    public void newsCrawlerFactoryTest_NullArgument() {
        new NewsCrawlerFactory(null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newsCrawlerControllerTest_NullArgument() {
        new NewsCrawlerController(null);
    }

    @Test(expected = NullPointerException.class)
    public void newsCrawlerControllerTest_NullConfiguration() {
        final NewsCrawlerFactory factory = new NewsCrawlerFactory(searchResultService, crawlerConfig);
        final NewsCrawlerController controller = new NewsCrawlerController(factory);
        controller.configure(null);
    }

}
