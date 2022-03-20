package ru.spbstu.news.searcher.controller;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.controller.result.SearchItem;
import ru.spbstu.news.searcher.util.SearcherTest;

import java.util.List;

public class SearchControllerTest extends SearcherTest {

    @Test
    public void findByText_NormalWork() throws Exception {
        storeTestData();
        MvcResult mvcResult = doRequestText()
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
    public void findImages_NormalWork() throws Exception {
        storeTestData();
        MvcResult mvcResult = doRequestImages()
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

    @Test
    public void findByText_NotFound() throws Exception {
        doRequestText(NOT_FOUND_QUERY)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    public void findImages_NotFound() throws Exception {
        doRequestImages(NOT_FOUND_QUERY)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
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
