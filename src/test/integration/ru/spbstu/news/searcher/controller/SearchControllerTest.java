package ru.spbstu.news.searcher.controller;

import org.hamcrest.Matchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.ImageItem;
import ru.spbstu.news.searcher.controller.result.SearchItem;
import ru.spbstu.news.searcher.util.SearcherTest;

import java.io.File;
import java.util.List;

@SpringBootTest(properties = { "indexer.indexDir=./indexText/SearchControllerTest" })
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

    @Test
    public void crawlNews_ControllerResponse() throws Exception {
        doRequestCrawl()
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andReturn();
        Assert.assertTrue(searchResultRepository.count() > 0);
    }

    @AfterClass
    public static void close() {
        deleteDirectory(new File("./indexText/SearchControllerTest"));
    }

}
