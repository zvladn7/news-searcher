package ru.spbstu.news.searcher.controller;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.spbstu.news.searcher.controller.request.FindRequest;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.exception.ResultNotFoundException;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.scanner.NewsCrawlerController;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class SearchControllerTest {

    private static final int PAGE = 1;

    @Mock
    private SearchResultService searchResultService;
    @Mock
    private NewsCrawlerController newsCrawlerController;

    private SearchController searchController;
    private FindRequest findRequest;
    private ItemToIndex itemToIndex;

    @Before
    public void setUp() {
        this.searchController = new SearchController(searchResultService, newsCrawlerController);
        this.findRequest = new FindRequest("футбол", 1);
        this.itemToIndex = new ItemToIndex("Url1", List.of("imageUrl1"), "футбол");
    }

    @Test(expected = NullPointerException.class)
    public void construct_searchResultServiceNull() {
        new SearchController(null, newsCrawlerController);
    }

    @Test(expected = NullPointerException.class)
    public void construct_newsCrawlerControllerNull() {
        new SearchController(searchResultService, null);
    }

    @Test(expected = NullPointerException.class)
    public void findByText_PageNull() {
        searchController.findByText(null, findRequest);
    }

    @Test(expected = NullPointerException.class)
    public void findByText_FindRequestNull() {
        searchController.findByText(PAGE, null);
    }

    @Test
    public void findByText_ResultOK() throws ResultNotFoundException {
        Mockito.doReturn(null)
                .when(searchResultService)
                .findByText(findRequest.getQuery(), PAGE);
        ResponseEntity<FindByTextResult> responseEntity = searchController.findByText(PAGE, findRequest);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void findByText_ResultNotFound() throws ResultNotFoundException {
        Mockito.doThrow(RuntimeException.class)
                .when(searchResultService)
                .findByText(findRequest.getQuery(), PAGE);
        ResponseEntity<FindByTextResult> responseEntity = searchController.findByText(PAGE, findRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test(expected = NullPointerException.class)
    public void findSimilar_FindRequestNull() {
        searchController.findSimilar(null);
    }

    @Test
    public void findSimilar_ResultOK() throws ResultNotFoundException {
        Mockito.doReturn(null)
                .when(searchResultService)
                .findSimilar(findRequest.getQuery());
        ResponseEntity<List<String>> responseEntity = searchController.findSimilar(findRequest);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void findSimilar_ResultNotFound() throws ResultNotFoundException {
        Mockito.doThrow(RuntimeException.class)
                .when(searchResultService)
                .findSimilar(findRequest.getQuery());
        ResponseEntity<List<String>> responseEntity = searchController.findSimilar(findRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }


    @Test(expected = NullPointerException.class)
    public void findImages_FindRequestNull() {
        searchController.findImages(null);
    }

    @Test
    public void findImages_ResultOK() throws ResultNotFoundException {
        Mockito.doReturn(null)
                .when(searchResultService)
                .findImages(findRequest.getPage(), findRequest.getQuery());
        ResponseEntity<FindImageResult> responseEntity = searchController.findImages(findRequest);
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void findImages_ResultNotFound() throws ResultNotFoundException {
        Mockito.doThrow(RuntimeException.class)
                .when(searchResultService)
                .findImages(findRequest.getPage(), findRequest.getQuery());
        ResponseEntity<FindImageResult> responseEntity = searchController.findImages(findRequest);
        Assert.assertEquals(HttpStatus.NOT_FOUND.value(), responseEntity.getStatusCode().value());
    }

    @Test(expected = NullPointerException.class)
    public void index_ItemToIndexNull() {
        searchController.index(null);
    }

    @Test
    public void index_ResultCreated() throws LuceneOpenException {
        Mockito.doNothing()
                .when(searchResultService)
                .index(itemToIndex);
        ResponseEntity<?> responseEntity = searchController.index(itemToIndex);
        Assert.assertEquals(HttpStatus.CREATED.value(), responseEntity.getStatusCode().value());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void index_ResultBadRequest() throws LuceneOpenException {
        Mockito.doThrow(LuceneOpenException.class)
                .when(searchResultService)
                .index(itemToIndex);
        ResponseEntity<?> responseEntity = searchController.index(itemToIndex);
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

    @Test
    public void crawl_ResultOK() throws Exception {
        Mockito.doNothing()
                .when(newsCrawlerController)
                .launchCrawling();
        ResponseEntity<?> responseEntity = searchController.crawl();
        Assert.assertEquals(HttpStatus.OK.value(), responseEntity.getStatusCode().value());
        Assert.assertNull(responseEntity.getBody());
    }

    @Test
    public void crawl_ResultBadRequest() throws Exception {
        Mockito.doThrow(RuntimeException.class)
                .when(newsCrawlerController)
                .launchCrawling();
        ResponseEntity<?> responseEntity = searchController.crawl();
        Assert.assertEquals(HttpStatus.BAD_REQUEST.value(), responseEntity.getStatusCode().value());
    }

}
