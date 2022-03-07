package ru.spbstu.news.searcher.scanner;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.url.WebURL;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.jsoup.nodes.Document;
import org.junit.Assert;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.util.Set;

@RunWith(MockitoJUnitRunner.class)
public class NewCrawlerTest {

    @Mock
    private SearchResultService searchResultService;

    private NewsCrawler newsCrawler;

    private static final String SMALL_IMAGE_URL = "https://2.imimg.com/data2/LQ/QV/MY-/teddy-small-size-500x500.jpg";
    private static final String NORMAL_IMAGE_URL = "https://helpx.adobe.com/content/dam/help/en/photoshop/how-to/compositing/jcr%3Acontent/main-pars/image/compositing_1408x792.jpg";
    private static final String WRONG_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/7/73/Deseret_small_long_O.svg/932px-Deseret_small_long_O.svg.png";
    private static final String ICON_IMAGE_URL = "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e0/Check_green_icon.svg/2048px-Check_green_icon.svg.png";
    private static final String WRONG_PARSING_URL = "https://vk.com/";
    private static final String CORRECT_PARSING_URL = "https://news.mail.ru/";
    @Mock
    private Document emptyDocument;
    private final WebURL emptyUrl = new WebURL();

    @Before
    public void before() {
        newsCrawler = new NewsCrawler(searchResultService);
    }

    @Test(expected = NullPointerException.class)
    public void filterImages_NullUrls() {
        newsCrawler.filterImages(null);
    }

    @Test
    public void filterImages_SmallSizeImage() {
        final WebURL url = new WebURL();
        url.setURL(SMALL_IMAGE_URL);
        Assert.assertTrue(newsCrawler.filterImages(Set.of(url)).isEmpty());
    }

    @Test
    public void filterImages_NormalSizeImage() {
        final WebURL url = new WebURL();
        url.setURL(NORMAL_IMAGE_URL);
        Assert.assertFalse(newsCrawler.filterImages(Set.of(url)).isEmpty());
    }

    @Test
    public void filterImages_WrongImage() {
        final WebURL url = new WebURL();
        url.setURL(WRONG_IMAGE_URL);
        Assert.assertTrue(newsCrawler.filterImages(Set.of(url)).isEmpty());
    }

    @Test
    public void filterImages_IconImage() {
        final WebURL url = new WebURL();
        url.setURL(ICON_IMAGE_URL);
        Assert.assertTrue(newsCrawler.filterImages(Set.of(url)).isEmpty());
    }

    @Test(expected = NullPointerException.class)
    public void filterImages_EmptyUrl() {
        newsCrawler.filterImages(Set.of(emptyUrl));
    }

    @Test(expected = NullPointerException.class)
    public void getTextFromPage_NullDocument() {
        newsCrawler.getText(null);
    }

    @Test(expected = NullPointerException.class)
    public void getTextFromPage_EmptyDocument() {
        newsCrawler.getText(emptyDocument);
    }

    @Test(expected = NullPointerException.class)
    public void shouldVisitUrl_EmptyUrl() {
        newsCrawler.shouldVisit(new Page(emptyUrl), emptyUrl);
    }

    @Test(expected = NullPointerException.class)
    public void shouldVisitUrl_NullUrl() {
        newsCrawler.shouldVisit(null, null);
    }

    @Test
    public void shouldVisitUrl_WrongUrl() {
        final WebURL url = new WebURL();
        url.setURL(WRONG_PARSING_URL);
        Assert.assertFalse(newsCrawler.shouldVisit(new Page(url), url));
    }

    @Test
    public void shouldVisitUrl_CorrectUrl() {
        final WebURL url = new WebURL();
        url.setURL(CORRECT_PARSING_URL);
        Assert.assertTrue(newsCrawler.shouldVisit(new Page(url), url));
    }

    @Test(expected = NullPointerException.class)
    public void visit_NullUrl() {
        newsCrawler.visit(null);
    }

}
