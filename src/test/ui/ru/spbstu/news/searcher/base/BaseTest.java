package ru.spbstu.news.searcher.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseTest {

    protected WebDriver driver;
    protected ObjectMapper mapper;

    @Before
    public void setUpClass() {
        mapper = new ObjectMapper();
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @After
    public void quit() {
        driver.quit();
    }

    protected void waitLoading(String componentClass) {
        WebDriverWait wait = new WebDriverWait(driver, 5);
        wait.until(ExpectedConditions.numberOfElementsToBe(By.className(componentClass), 1));
    }

    protected WebElement getSearchInput() {
        return driver.findElement(By.className("input__input-text"));
    }

    protected WebElement getSearchButton() {
        return driver.findElement(By.className("search-button"));
    }

    protected WebElement getClearButton() {
        return driver.findElement(By.className("input-search__icon--click-effect"));
    }

    protected WebElement getLanguageButton() {
        return driver.findElement(By.className("language-button"));
    }

}
