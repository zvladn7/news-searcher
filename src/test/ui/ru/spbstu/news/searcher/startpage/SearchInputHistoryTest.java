package ru.spbstu.news.searcher.startpage;

import ru.spbstu.news.searcher.base.BaseTest;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.html5.LocalStorage;
import org.openqa.selenium.html5.WebStorage;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchInputHistoryTest extends BaseTest {

    private static final String LOCAL_STORAGE_KEY = "news-searcher-queries-history";
    private static final int HISTORY_LIST_DISPLAY_LIMIT = 10;
    private static final int HISTORY_ALL_ELEMENTS_LIMIT = 100;

    private LocalStorage localStorage;

    @Before
    public void generateHistoryInLocalStorage() throws JsonProcessingException {
        localStorage = ((WebStorage) driver).getLocalStorage();
        Object[] historyElements = IntStream.range(1, HISTORY_ALL_ELEMENTS_LIMIT + 1)
                .mapToObj(String::valueOf)
                .toArray();
        localStorage.setItem(LOCAL_STORAGE_KEY, mapper.writeValueAsString(historyElements));
        driver.navigate().refresh();
    }

    @Test
    public void textInput_OpenHistory() {
        getSearchInput().click();
        Assert.assertTrue(getHistoryList().isDisplayed());
    }

    @Test(expected = org.openqa.selenium.NoSuchElementException.class)
    public void textInput_CloseHistory() {
        getSearchInput().click();
        Assert.assertTrue(getHistoryList().isDisplayed());
        getSearchInput().sendKeys("text for search");
        getHistoryList().isDisplayed();
    }

    @Test
    public void textInput_HistoryTest() {
        getSearchInput().click();
        List<WebElement> historyListElements
                = getHistoryList().findElements(By.className("input-search__history-item"));
        Assert.assertEquals(HISTORY_LIST_DISPLAY_LIMIT, historyListElements.size());
        checkHistoryElementsValue(historyListElements, 1, HISTORY_LIST_DISPLAY_LIMIT);
    }

    @Test
    public void history_displaceLatestElementOnSearch() throws JsonProcessingException {
        String textForSearch = "text for search";
        getSearchInput().sendKeys(textForSearch);
        getSearchButton().click();
        driver.navigate().back();
        getSearchInput().click();
        List<WebElement> historyListElements = getHistoryListElements();
        Assert.assertEquals(HISTORY_LIST_DISPLAY_LIMIT, historyListElements.size());
        Assert.assertEquals(textForSearch, historyListElements.get(0).getText());
        String historyElementsJson = localStorage.getItem(LOCAL_STORAGE_KEY);
        String[] historyStringElements = mapper.readValue(historyElementsJson, String[].class);
        Assert.assertEquals(HISTORY_ALL_ELEMENTS_LIMIT, historyStringElements.length);
        Assert.assertFalse(Stream.of(historyStringElements)
                .filter(text -> !textForSearch.equals(text))
                .mapToInt(Integer::parseInt)
                .boxed()
                .collect(Collectors.toList())
                .contains(100));
    }

    @Test
    public void history_deleteElement() {
        getSearchInput().click();
        getHistoryListElements().get(0)
                .findElement(By.className("input-search__history-item-icon--trash"))
                .click();
        checkHistoryElementsValue(getHistoryListElements(), 2, 1 + HISTORY_LIST_DISPLAY_LIMIT);
    }

    private void checkHistoryElementsValue(@NotNull List<WebElement> historyListElements,
                                           int from,
                                           int to) {
        for (int i = from; i <= to; i++) {
            Assert.assertEquals(String.valueOf(i), historyListElements.get(i - from).getText());
        }
    }

    @After
    public void clearHistoryInLocalStorage() {
        localStorage.setItem(LOCAL_STORAGE_KEY, "");
    }

    public WebElement getHistoryList() {
        return driver.findElement(By.className("input-search__history"));
    }

    public List<WebElement> getHistoryListElements() {
        return getHistoryList().findElements(By.className("input-search__history-item"));
    }

}
