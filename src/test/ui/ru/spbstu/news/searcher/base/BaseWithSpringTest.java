package ru.spbstu.news.searcher.base;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.spbstu.news.searcher.base.BaseTest;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.mainpage.components.SearchImageContent;
import ru.spbstu.news.searcher.mainpage.components.SearchTextContent;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:ui-test.properties")
public abstract class BaseWithSpringTest extends BaseTest {

    protected static final String URL = "https://sportmail.ru/news/hockey-khl/50472533/?frommail=1";
    protected static final List<String> IMAGE_URLS = Collections.singletonList("https://resizer.mail.ru/p/029b7b3b-72c1-532c-bb37-40c4851cc437/AQAGuWZsJdhoPFzs3HXEgVkmBsz9481Y9l4I4ouAJmS9-XlN1x_an6nP9YPr8fotCydNg_IUVYl_X-UaeXZdLkUgges.jpg");
    protected static final String TEXT = "Ранее президент московского клуба Виктор Воронин сообщил, что руководство команды будет общаться с легионерами на предмет их участия в следующем раунде плей-офф КХЛ.\n" +
            "Помимо О’Делла и Петерссона, в составе «Динамо» из иностранцев выступают шведы Антон Ведин, Оскар Линдберг, а также канадец Роб Клинкхаммер.\n" +
            "Во втором раунде плей-офф КХЛ «Динамо» сыграет с ЦСКА, серия стартует 17 марта.";
    protected static final String QUERY = "ЦСКА";
    protected static final String CONTENT_TYPE = "application/json";

    @Autowired
    protected WebApplicationContext webApplicationContext;
    protected MockMvc mockMvc;

    @Before
    public void setUpMvc() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    }

    protected void storeTestData() throws Exception {
        ItemToIndex itemToIndex = new ItemToIndex(URL, IMAGE_URLS, TEXT);
        String jsonItemToIndex = mapper.writeValueAsString(itemToIndex);
        MockHttpServletRequestBuilder indexMessageRequestBuilder = MockMvcRequestBuilders.post("/search/index")
                .content(jsonItemToIndex)
                .contentType(CONTENT_TYPE);
        this.mockMvc.perform(indexMessageRequestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }


    public void prepareTextResults() {
        getSearchInput().clear();
        getSearchInput().sendKeys(QUERY);
        getSearchButton().click();
        waitTextResults();
    }

    public void waitTextResults() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(SearchTextContent.SEARCH_CONTENT_ITEM_CLASS)));
    }

    public void waitImageResults() {
        WebDriverWait wait = new WebDriverWait(driver, 10);
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By.className(SearchImageContent.SEARCH_CONTENT_ITEM_CLASS)));
    }


}
