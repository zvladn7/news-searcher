package ru.spbstu.news.searcher.mainpage;

import org.hamcrest.Matchers;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.spbstu.news.searcher.base.BaseWithSpringTest;
import ru.spbstu.news.searcher.mainpage.components.*;
import ru.spbstu.news.searcher.service.TitleExtractor;

import java.io.File;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:ui-test.properties")
@SpringBootTest(
        properties = {
                "indexer.indexDir=./indexText/MainPageTest",
                "server.port=9090",
        },
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class MainPageTest extends BaseWithSpringTest {

    private static final int MAX_PAGE_CONTENT_ITEMS = 10;
    private static final int MAX_SIMILAR_ITEMS = 8;

    @Autowired
    private TitleExtractor titleExtractor;

    @AfterClass
    public static void close() {
        deleteDirectory(new File("./indexText/MainPageTest"));
    }

    public static void deleteDirectory(File file) {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 25; ++i) {
            storeTestData();
        }
    }

    @Test
    public void textResult_bySearch() {
        prepareTextResults();
        SearchTextContent searchContent = new SearchTextContent(driver);
        Assert.assertEquals(MAX_PAGE_CONTENT_ITEMS, searchContent.getContentItemsSize());
        SearchTextResult resultItem = searchContent.getResultItem(0);
        Assert.assertEquals(URL, resultItem.getUrl());
        Assert.assertEquals(titleExtractor.getTitleFromFullText(TEXT, 1L, QUERY).trim(), resultItem.getTitle());
        resultItem.clickLink();
        checkClickedItemUrl(resultItem.getUrl());
    }

    public void checkClickedItemUrl(String url) {
        String urlHandle = driver.getWindowHandles()
                .stream()
                .filter(handle -> !handle.equals(driver.getWindowHandle()))
                .findFirst().orElseGet(null);
        driver.switchTo().window(urlHandle);
        Assert.assertEquals(driver.getCurrentUrl(), url);
        driver.close();
    }

    @Test
    public void textResult_similar() {
        prepareTextResults();
        WebElement similarElements = driver.findElement(By.className("similar-queries"));
        List<WebElement> similarElementsList = similarElements.findElements(By.className("similar-queries__item"));
        Assert.assertEquals(MAX_SIMILAR_ITEMS, similarElementsList.size());
        for (WebElement similarElement : similarElementsList) {
            Assert.assertTrue(similarElement.findElement(By.className("similar-queries__item-text")).getText().contains(QUERY));
        }
        WebElement firstSimilar = similarElementsList.get(0);
        String queryText = firstSimilar.findElement(By.className("similar-queries__item-text")).getText();
        firstSimilar.click();
        waitTextResults();
        Assert.assertEquals(queryText, getSearchInput().getAttribute("value"));
    }

    @Test
    public void paginator() {
        prepareTextResults();
        Paginator paginator = new Paginator(driver);
        Assert.assertThat(paginator.pagesSize(), Matchers.lessThanOrEqualTo(7));
        paginator.navigate(2);
        Assert.assertTrue(driver.getCurrentUrl().contains("page=1"));
    }

    @Test
    public void searchTabsChange() {
        prepareTextResults();
        Navigator navigator = new Navigator(driver);
        navigator.navigateToImageTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("tab=" + Navigator.IMAGE_TAB_INDEX));
        navigator.navigateToTextTab();
        Assert.assertTrue(driver.getCurrentUrl().contains("tab=" + Navigator.TEXT_TAB_INDEX));
    }

    @Test
    public void imageResults() {
        prepareTextResults();
        Navigator navigator = new Navigator(driver);
        navigator.navigateToImageTab();
        waitImageResults();
        SearchImageContent imageContent = new SearchImageContent(driver);
        SearchImageResult resultItem = imageContent.getResultItem(0);
        Assert.assertEquals(URL, resultItem.getUrl());
        Assert.assertEquals(titleExtractor.getTitleFromFullText(TEXT, 1L, QUERY).trim(), resultItem.getTitle());
        resultItem.clickLink();
        checkClickedItemUrl(resultItem.getUrl());
    }

}
