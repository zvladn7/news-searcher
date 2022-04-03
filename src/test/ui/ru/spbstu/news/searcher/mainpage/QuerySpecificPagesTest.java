package ru.spbstu.news.searcher.mainpage;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import ru.spbstu.news.searcher.base.BaseTest;

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
public class QuerySpecificPagesTest extends BaseTest {

    private static final String NOT_FOUND_COMPONENT_CLASS = "not-found";
    private static final String INCORRECT_REQUEST_CLASS = "break-rules";

    @Before
    public void setUp() {
        getSearchInput().sendKeys("1");
        getSearchButton().click();
    }

    @Test
    public void textResults_NotFound() {
        checkTermBlock("пришелец", NOT_FOUND_COMPONENT_CLASS, "not-found__query");
    }

    @Test
    public void textResults_IncorrectRequest() {
        checkTermBlock("123]", INCORRECT_REQUEST_CLASS, "break-rules__query");
    }

    public void checkTermBlock(String query, String messageClass, String queryClass) {
        getSearchInput().clear();
        getSearchInput().sendKeys(query);
        getSearchButton().click();
        waitLoading(messageClass);
        WebElement notFoundElement = driver.findElement(By.className(queryClass));
        Assert.assertEquals(query, notFoundElement.getText());
        Assert.assertThat(
                driver.findElement(By.className("terms-block__content"))
                        .findElements(By.className("terms-block__item"))
                        .size(),
                Matchers.greaterThan(0)
        );
    }


}
