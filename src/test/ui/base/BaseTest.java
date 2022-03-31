package base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    protected static WebDriver driver;
    protected static ObjectMapper mapper;

    @BeforeClass
    public static void setUpClass() {
        mapper = new ObjectMapper();
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @AfterClass
    public static void shutdownClass() {
        driver.close();
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

}
