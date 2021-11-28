package ru.spbstu.news.searcher.controller.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchItem {

    private final long id;
    private final String title;
    private final String link;

}
