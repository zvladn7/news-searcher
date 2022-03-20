package ru.spbstu.news.searcher.crawler;

import org.junit.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.controller.result.SearchItem;
import ru.spbstu.news.searcher.database.SearchResult;
import ru.spbstu.news.searcher.database.SearchResultRepository;
import ru.spbstu.news.searcher.exception.ResultNotFoundException;
import ru.spbstu.news.searcher.scanner.NewsCrawlerController;
import ru.spbstu.news.searcher.service.SearchResultService;
import ru.spbstu.news.searcher.util.SearcherTest;

import java.io.File;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@SpringBootTest(properties = {"indexer.indexDir=./indexText/CrawlerTest"})
public class CrawlerTest extends SearcherTest {

    @Autowired
    private NewsCrawlerController newsCrawlerController;
    @Autowired
    private SearchResultRepository searchResultRepository;
    @Autowired
    private SearchResultService searchResultService;

    private static final Integer PAGE = 1;
    private final String ARTICLE_TEXT = "Ранее в МИД Финляндии сообщили, что координационная";
    private final String CORRECT_IMAGE_URL = "https://retina.news.mail.ru/prev780x440/pic/4d/32/image50519367_439a92e8027e918d79f7d3c9c1344b33.jpg";

    @Before
    public void setUp() throws Exception {
        newsCrawlerController.launchCrawling();
    }

    @After
    public void clear() {
        searchResultRepository.deleteAll();
    }

    @Test
    public void crawlNews_CheckDatabaseFill() {
        Assert.assertTrue(searchResultRepository.count() > 0);
    }

    @Test
    public void crawlNews_CheckTextCorrectness() throws ResultNotFoundException {
        final FindByTextResult result = searchResultService.findByText(ARTICLE_TEXT, PAGE);
        Assert.assertEquals(result.getSearchItems().size(), searchResultRepository.count());
        final SearchItem searchItem = result.getSearchItems().get(0);
        final String resultTitle = searchItem.getTitle().toLowerCase(Locale.ROOT);
        boolean correctResult = false;
        for (final String word : ARTICLE_TEXT.toLowerCase(Locale.ROOT).split(" ")) {
            if (correctResult) {
                break;
            }
            correctResult = resultTitle.contains(word);
        }
        Assert.assertTrue(correctResult);
    }

    @Test(expected = ResultNotFoundException.class)
    public void crawlNews_CheckWrongText() throws ResultNotFoundException {
        final String randomText = UUID.randomUUID().toString();
        searchResultService.findByText(randomText, PAGE);
    }

    @Test
    public void crawlNews_CheckImageCorrectness() {
        final SearchResult searchResult = searchResultRepository.findAll().get(0);
        final List<String> images = searchResult.getImageUrls();
        Assert.assertTrue(images.size() > 0);
        Assert.assertTrue(images.contains(CORRECT_IMAGE_URL));
    }


    @Test
    public void crawlNews_SearchImage() throws ResultNotFoundException {
        final FindImageResult result = searchResultService.findImages(PAGE, ARTICLE_TEXT);
        final SearchResult savedItem = searchResultRepository.findAll().get(0);
        Assert.assertEquals(result.getImageItems().size(), savedItem.getImageUrls().size());
        final ImageItem foundImage = result.getImageItems().get(0);
        Assert.assertEquals(savedItem.getImageUrls().get(0), foundImage.getImageUrl());
    }

    @AfterClass
    public static void close() {
        deleteDirectory(new File("./indexText/CrawlerTest"));
    }

}
