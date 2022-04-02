package ru.spbstu.news.searcher.mainpage.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class SearchTextResult {

    private static final String TITLE_CLASS = "block-result-search__title";
    private static final String LINK_CLASS = "block-result-search__link";

    private final WebElement clickable;
    private final String title;
    private final String url;

    public SearchTextResult(WebElement contentItem) {
        this.clickable = contentItem.findElement(By.className(LINK_CLASS));
        this.title = contentItem.findElement(By.className(TITLE_CLASS)).getText();
        this.url = clickable.getText();
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public void clickLink() {
        clickable.click();
    }

}
