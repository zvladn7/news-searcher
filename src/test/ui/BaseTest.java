import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

public class BaseTest {

    protected static WebDriver driver;

    @BeforeClass
    public static void setUp() {
        System.setProperty("webdriver.chrome.driver", "/usr/local/bin/chromedriver");
        driver = new ChromeDriver();
        driver.get("http://localhost:3000");
    }

    @Test
    public void t() {
        WebElement element = driver.findElement(By.className("input__input-text"));
        String text = element.getText();
        Assert.assertEquals("", text);
        Assert.assertEquals("Найти в новостях...", element.getAttribute("placeholder"));
    }

}
