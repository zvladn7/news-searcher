package ru.spbstu.news.searcher.mainpage;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.spbstu.news.searcher.base.BaseWithSpringTest;
import ru.spbstu.news.searcher.mainpage.components.Navigator;

import java.util.Locale;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:ui-test.properties")
@SpringBootTest(
        properties = {
                "indexer.indexDir=./indexText/LanguageChangeTest",
                "server.port=9090",
        }
)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class LanguageChangeTest extends BaseWithSpringTest {

    private static final String LANGUAGE_LOCAL_STORAGE_KEY = "news-searcher-language";
    private static final String LANGUAGE_RU = "RU";
    private static final String LANGUAGE_EN = "EN";

    private static final String[] RU = {
            "Найти в новостях...",
            "ПОИСК",
            "РУССКИЙ",
            "ТЕКСТ",
            "Картинки",
            "Около",
            "результатов",
            "Похожие поисковые запросы",
            "Ваш поиск",
            "не соответствует ни одному документу.",
            "Предложения:",
            "нарушает следующие правила запроса:",
            "Правила поискового запроса:",
    };

    private static final String[] EN = {
            "Search in news...",
            "SEARCH",
            "ENGLISH",
            "TEXT",
            "Images",
            "About",
            "results",
            "Related searches",
            "Your search",
            "did not match any documents.",
            "Suggestions:",
            "breaks the rules:",
            "Search query rules:"
    };

    @Before
    public void setUp() throws Exception {
        for (int i = 0; i < 10; ++i) {
            storeTestData();
        }
        changeLanguageToRuIfNeed();
    }

    @Test
    public void checkLanguageRU() {
        prepareTextResults();
        checkLanguage(RU);
    }

    @Test
    public void checkLanguageEN() {
        prepareTextResults();
        getLanguageButton().click();
        checkLanguage(EN);
    }

    public void checkLanguage(String[] constants) {
        Assert.assertEquals(constants[0], getSearchInput().getAttribute("placeholder"));
        Assert.assertEquals(constants[1], getSearchButton().getText());
        Assert.assertEquals(constants[2], getLanguageButton().findElement(By.className("language-button__text")).getText());
        Navigator navigator = new Navigator(driver);
        Assert.assertEquals(constants[3], navigator.getTextTabText());
        Assert.assertEquals(constants[4].toLowerCase(Locale.ROOT), navigator.getImageTabText().toLowerCase(Locale.ROOT));
        WebElement about = driver.findElement(By.className("search-count"));
        Assert.assertTrue(about.getText().contains(constants[5]));
        Assert.assertTrue(about.getText().contains(constants[6]));
        Assert.assertEquals(constants[7], driver.findElement(By.className("similar-queries__title")).getText());
        getSearchInput().clear();
        getSearchInput().sendKeys("123123rf");
        getSearchButton().click();
        waitLoading("not-found__warning");
        String notFoundWarningText = driver.findElement(By.className("not-found__warning")).getText();
        Assert.assertTrue(notFoundWarningText.contains(constants[8]));
        Assert.assertTrue(notFoundWarningText.contains(constants[9]));
        Assert.assertEquals(constants[10], driver.findElement(By.className("terms-block__title")).getText());
        getSearchInput().clear();
        getSearchInput().sendKeys("1233[");
        getSearchButton().click();
        waitLoading("break-rules__warning");
        String incorrectRequestWarningText = driver.findElement(By.className("break-rules__warning")).getText();
        Assert.assertTrue(incorrectRequestWarningText.contains(constants[8]));
        Assert.assertTrue(incorrectRequestWarningText.contains(constants[11]));
        Assert.assertEquals(constants[12], driver.findElement(By.className("terms-block__title")).getText());
    }

    @After
    public void changeLanguageAfterTest() {
        changeLanguageToRuIfNeed();
    }

    public void changeLanguageToRuIfNeed() {
        LocalStorage localStorage = ((WebStorage) driver).getLocalStorage();
        if (LANGUAGE_EN.equals(localStorage.getItem(LANGUAGE_LOCAL_STORAGE_KEY))) {
            localStorage.setItem(LANGUAGE_LOCAL_STORAGE_KEY, LANGUAGE_RU);
        }
        driver.navigate().refresh();
    }


}
