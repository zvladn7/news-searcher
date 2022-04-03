package ru.spbstu.news.searcher.mainpage.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Paginator {

    private static final String SEARCH_CONTENT_PAGINATOR_CLASS = "search__content-paginate";
    private static final String SEARCH_CONTENT_PAGINATOR_ITEM_CLASS = "search__content-paginate-li";
    private static final String SEARCH_CONTENT_PAGINATOR_BUTTONS = "search__content-paginate-button";

    private final WebElement paginator;
    private final List<WebElement> paginatorItems;
    private final List<WebElement> paginatorButtons;

    private int page = 1;

    public Paginator(WebDriver driver) {
        this.paginator = driver.findElement(By.className(SEARCH_CONTENT_PAGINATOR_CLASS));
        this.paginatorItems = paginator.findElements(By.className(SEARCH_CONTENT_PAGINATOR_ITEM_CLASS));
        this.paginatorButtons = paginator.findElements(By.className(SEARCH_CONTENT_PAGINATOR_BUTTONS));
    }

    public int pagesSize() {
        return paginatorItems.size();
    }

    public void navigate(int page) {
        paginatorItems.get(page - 1).click();
    }

    public int navigateButtonsSize() {
        return paginatorButtons.size();
    }

}
