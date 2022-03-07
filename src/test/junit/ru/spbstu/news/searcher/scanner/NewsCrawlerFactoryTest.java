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

    @Test(expected = IllegalArgumentException.class)
    public void newsCrawlerFactoryTest_NullArgument() {
        new NewsCrawlerFactory(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void newsCrawlerControllerTest_NullArgument() {
        new NewsCrawlerController(null);
    }

    @Test(expected = NullPointerException.class)
    public void newsCrawlerControllerTest_NullConfiguration() {
        final NewsCrawlerFactory factory = new NewsCrawlerFactory(searchResultService);
        final NewsCrawlerController controller = new NewsCrawlerController(factory);
        controller.configure(null);
    }

}
