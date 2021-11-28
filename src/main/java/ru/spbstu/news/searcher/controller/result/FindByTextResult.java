package ru.spbstu.news.searcher.controller.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class FindByTextResult {

    private final List<SearchItem> searchItems;
    private final long totalCount;

}
