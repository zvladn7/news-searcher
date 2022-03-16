package ru.spbstu.news.searcher.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.spbstu.news.searcher.controller.request.FindRequest;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.controller.result.SearchItem;

import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class SearchControllerTest {

    private static final String URL = "https://sportmail.ru/news/hockey-khl/50472533/?frommail=1";
    private static final List<String> IMAGE_URLS = Collections.singletonList("https://resizer.mail.ru/p/029b7b3b-72c1-532c-bb37-40c4851cc437/AQAGuWZsJdhoPFzs3HXEgVkmBsz9481Y9l4I4ouAJmS9-XlN1x_an6nP9YPr8fotCydNg_IUVYl_X-UaeXZdLkUgges.jpg");
    private static final String TEXT = "Ранее президент московского клуба Виктор Воронин сообщил, что руководство команды будет общаться с легионерами на предмет их участия в следующем раунде плей-офф КХЛ.\n" +
            "Помимо О’Делла и Петерссона, в составе «Динамо» из иностранцев выступают шведы Антон Ведин, Оскар Линдберг, а также канадец Роб Клинкхаммер.\n" +
            "Во втором раунде плей-офф КХЛ «Динамо» сыграет с ЦСКА, серия стартует 17 марта.";

    private static final Integer PAGE = 1;
    private static final String QUERY = "динамо";

    private static final String CONTENT_TYPE = "application/json";

    @Autowired
    private WebApplicationContext webApplicationContext;
    private MockMvc mockMvc;
    private ObjectMapper mapper;

    @Before
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.mapper = new ObjectMapper();
    }

    @Test
    public void findByText_NormalWork() throws Exception {
        storeTestData();
        FindRequest findRequest = new FindRequest(QUERY, PAGE);
        String jsonFindRequest = mapper.writeValueAsString(findRequest);
        MockHttpServletRequestBuilder searchMessageRequestBuilder = MockMvcRequestBuilders.post("/search/" + PAGE)
                .content(jsonFindRequest)
                .contentType(CONTENT_TYPE);
        MvcResult mvcResult = this.mockMvc.perform(searchMessageRequestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertEquals(CONTENT_TYPE, response.getContentType());
        String contentAsString = response.getContentAsString();
        FindByTextResult textResult = mapper.readValue(contentAsString, FindByTextResult.class);
        List<SearchItem> searchItems = textResult.getSearchItems();
        SearchItem testSearchItem = new SearchItem(1, "some title", URL);
        Assert.assertThat(searchItems, Matchers.hasItems(testSearchItem));
    }

    @Test
    public void findImages() throws Exception {
        storeTestData();
        FindRequest findRequest = new FindRequest(QUERY, PAGE);
        String jsonFindRequest = mapper.writeValueAsString(findRequest);
        MockHttpServletRequestBuilder searchMessageRequestBuilder = MockMvcRequestBuilders.post("/search/" + PAGE)
                .content(jsonFindRequest)
                .contentType(CONTENT_TYPE);
        MvcResult mvcResult = this.mockMvc.perform(searchMessageRequestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
                Assert.assertEquals(CONTENT_TYPE, response.getContentType());
        String contentAsString = response.getContentAsString();
        FindImageResult textResult = mapper.readValue(contentAsString, FindImageResult.class);
        List<ImageItem> imageItems = textResult.getImageItems();
        ImageItem testImageItem = new ImageItem(1, IMAGE_URLS.get(0), "some title", URL);
        Assert.assertThat(imageItems, Matchers.hasItems(testImageItem));
    }

    public void storeTestData() throws Exception {
        ItemToIndex itemToIndex = new ItemToIndex(URL, IMAGE_URLS, TEXT);
        String jsonItemToIndex = mapper.writeValueAsString(itemToIndex);
        MockHttpServletRequestBuilder indexMessageRequestBuilder = MockMvcRequestBuilders.post("/search/index")
                .content(jsonItemToIndex)
                .contentType(CONTENT_TYPE);
        this.mockMvc.perform(indexMessageRequestBuilder)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isCreated());
    }

    /**
     * FYI: Починить траблы с индексами при запуске тестов
     * -----
     * тесты:
     * 1. Тесты с неправильными запросами/параметры - NotFound, BadRequest
     * 2. Тесты интеграции базы данных и индексов
     * 3. Тесты на валидацию данных в кеше (пример, делаем запрос через контроллер -> проверям, что данные оказались кэше)
     * 4. Интеграционные тест на кравлер (Илья - например, все новости из заданных источников)
     * 5. подумать, что можно еще
     * -----
     * Настроить CI в GitHub Actions, чтобы запускался Docker
     */

}
