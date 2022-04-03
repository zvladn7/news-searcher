package ru.spbstu.news.searcher.mainpage.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class Navigator {

    private static final String TABS_CLASS = "tabs";
    private static final String TABS_TITLE_CLASS = "tabs__item-title";

    public static final int TEXT_TAB_INDEX = 0;
    public static final int IMAGE_TAB_INDEX = 1;

    private final WebElement textTab;
    private final WebElement imageTab;

    public Navigator(WebDriver driver) {
        WebElement tabs = driver.findElement(By.className(TABS_CLASS));
        List<WebElement> tabsElements = tabs.findElements(By.className(TABS_TITLE_CLASS));
        this.textTab = tabsElements.get(TEXT_TAB_INDEX);
        this.imageTab = tabsElements.get(IMAGE_TAB_INDEX);
    }

    public String getTextTabText() {
        return textTab.getText();
    }

    public String getImageTabText() {
        return imageTab.getText();
    }

    public void navigateToTextTab() {
        textTab.click();
    }

    public void navigateToImageTab() {
        imageTab.click();
    }

}
