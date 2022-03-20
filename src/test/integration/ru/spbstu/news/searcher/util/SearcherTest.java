package ru.spbstu.news.searcher.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import ru.spbstu.news.searcher.controller.request.FindRequest;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.database.SearchResultRepository;

import java.io.File;
import java.util.Collections;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations="classpath:test.properties")
public abstract class SearcherTest {

    protected static final String URL = "https://sportmail.ru/news/hockey-khl/50472533/?frommail=1";
    protected static final List<String> IMAGE_URLS = Collections.singletonList("https://resizer.mail.ru/p/029b7b3b-72c1-532c-bb37-40c4851cc437/AQAGuWZsJdhoPFzs3HXEgVkmBsz9481Y9l4I4ouAJmS9-XlN1x_an6nP9YPr8fotCydNg_IUVYl_X-UaeXZdLkUgges.jpg");
    protected static final String TEXT = "Ранее президент московского клуба Виктор Воронин сообщил, что руководство команды будет общаться с легионерами на предмет их участия в следующем раунде плей-офф КХЛ.\n" +
            "Помимо О’Делла и Петерссона, в составе «Динамо» из иностранцев выступают шведы Антон Ведин, Оскар Линдберг, а также канадец Роб Клинкхаммер.\n" +
            "Во втором раунде плей-офф КХЛ «Динамо» сыграет с ЦСКА, серия стартует 17 марта.";

    protected static final Integer PAGE = 1;
    protected static final String QUERY = "динамо";
    protected static final String NOT_FOUND_QUERY = "not_found_query";

    protected static final String CONTENT_TYPE = "application/json";

    @Autowired
    protected WebApplicationContext webApplicationContext;
    @Autowired
    protected SearchResultRepository searchResultRepository;
    protected MockMvc mockMvc;
    protected ObjectMapper mapper;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
        this.mapper = new ObjectMapper();
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

    public ResultActions doRequestText() throws Exception {
        return doRequest("/search/" + PAGE, QUERY);
    }

    public ResultActions doRequestText(String query) throws Exception {
        return doRequest("/search/" + PAGE, query);
    }

    public ResultActions doRequestImages() throws Exception {
        return doRequest("/search/image", QUERY);
    }

    public ResultActions doRequestImages(String query) throws Exception {
        return doRequest("/search/image", query);
    }

    public ResultActions doRequestCrawl() throws Exception {
        return doGetRequest("/search/crawl");
    }

    public ResultActions doRequest(@NotNull String path,
                                   @NotNull String query) throws Exception {
        Validate.notNull(path);
        FindRequest findRequest = new FindRequest(query, PAGE);
        String jsonFindRequest = mapper.writeValueAsString(findRequest);
        MockHttpServletRequestBuilder searchMessageRequestBuilder = MockMvcRequestBuilders.post(path)
                .content(jsonFindRequest)
                .contentType(CONTENT_TYPE);
        return this.mockMvc.perform(searchMessageRequestBuilder);
    }

    public ResultActions doGetRequest(@NotNull String path) throws Exception {
        Validate.notNull(path);
        MockHttpServletRequestBuilder searchMessageRequestBuilder = MockMvcRequestBuilders.get(path)
                .contentType(CONTENT_TYPE);
        return this.mockMvc.perform(searchMessageRequestBuilder);
    }

    public static void deleteDirectory(File file)
    {
        for (File subfile : file.listFiles()) {
            if (subfile.isDirectory()) {
                deleteDirectory(subfile);
            }
            subfile.delete();
        }
    }


}
