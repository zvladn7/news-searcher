package startpage;

import base.BaseTest;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.openqa.selenium.WebElement;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SearchStartPageTest extends BaseTest {

    @Test
    public void textInput_OnEmtpyInputTextPlaceholder() {
        WebElement element = getSearchInput();
        String text = element.getText();
        Assert.assertEquals("", text);
        Assert.assertEquals("Найти в новостях...", element.getAttribute("placeholder"));
    }

    @Test(expected = org.openqa.selenium.NoSuchElementException.class)
    public void searchButton_DisabledOnEmtpyInputText() {
        getSearchButton();
    }

    @Test
    public void searchButton_enabledOnTextInput() {
        WebElement input = getSearchInput();
        input.sendKeys("text for search");
//        WebDriverWait wait = new WebDriverWait(driver,30);
//        wait.until(ExpectedConditions.visibilityOfElementLocated(By.className("search-button")));
        Assert.assertTrue(getSearchButton().isDisplayed());
    }

    @Test
    public void textInput_ClearText() {
        WebElement input = getSearchInput();
        input.sendKeys("text for search");
        getClearButton().click();
        Assert.assertEquals("", input.getText());
    }

}
