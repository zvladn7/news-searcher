package ru.spbstu.news.searcher.mainpage.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.stream.Collectors;

public class SearchTextContent {

    public static final String SEARCH_CONTENT_CLASS = "search__content";
    public static final String SEARCH_CONTENT_ITEM_CLASS = "block-result-search";

    private final List<SearchTextResult> searchContentItems;

    public SearchTextContent(WebDriver driver) {
        WebElement content = driver.findElement(By.className(SEARCH_CONTENT_CLASS));
        this.searchContentItems = content.findElements(By.className(SEARCH_CONTENT_ITEM_CLASS))
                .stream()
                .map(SearchTextResult::new)
                .collect(Collectors.toList());
    }

    public int getContentItemsSize() {
        return searchContentItems.size();
    }

    public SearchTextResult getResultItem(int index) {
        return searchContentItems.get(index);
    }

}
