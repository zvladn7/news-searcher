package ru.spbstu.news.searcher.controller;


import org.apache.commons.lang3.Validate;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.spbstu.news.searcher.controller.request.FindImagesRequest;
import ru.spbstu.news.searcher.controller.request.ItemToIndex;
import ru.spbstu.news.searcher.controller.result.FindByTextResult;
import ru.spbstu.news.searcher.controller.result.FindImageResult;
import ru.spbstu.news.searcher.controller.result.SimilarItem;
import ru.spbstu.news.searcher.indexes.exceptions.LuceneOpenException;
import ru.spbstu.news.searcher.scanner.NewsCrawlerController;
import ru.spbstu.news.searcher.service.SearchResultService;

import java.util.List;

@RestController
@RequestMapping(value = "/search")
public class SearchController {

    @Autowired
    private SearchResultService searchResultService;

    @Autowired
    private NewsCrawlerController newsCrawlerController;

    @GetMapping("/{page}")
    public ResponseEntity<FindByTextResult> findByText(@PathVariable(name = "page") Integer page,
                                                       @RequestBody String query) {
        Validate.notNull(page);
        Validate.notNull(query);
        try {
            FindByTextResult textResult = searchResultService.findByText(query, page);
            return ResponseEntity.ok(textResult);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/similar")
    public ResponseEntity<List<SimilarItem>> findSimilar(@NotNull @RequestBody String query) {
        Validate.notNull(query);
        try {
            List<SimilarItem> similarItems = searchResultService.findSimilar(query);
            return ResponseEntity.ok(similarItems);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/image")
    public ResponseEntity<FindImageResult> findImages(@RequestBody FindImagesRequest imagesRequest) {
        Validate.notNull(imagesRequest);
        Validate.notNull(imagesRequest.getQuery());
        try {
            FindImageResult imageResults = searchResultService.findImages(imagesRequest.getPage(), imagesRequest.getQuery());
            return ResponseEntity.ok(imageResults);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/index")
    public ResponseEntity<?> index(@NotNull @RequestBody ItemToIndex itemToIndex) {
        Validate.notNull(itemToIndex);
        try {
            searchResultService.index(itemToIndex);
            return new ResponseEntity<>(HttpStatus.CREATED);
        } catch (LuceneOpenException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/crawl")
    public ResponseEntity<?> crawl() {
        try {
            newsCrawlerController.launchCrawling();
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

}
