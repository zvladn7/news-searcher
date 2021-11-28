package ru.spbstu.news.searcher.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FindImagesRequest {

    private final String query;
    private final int page;

}
