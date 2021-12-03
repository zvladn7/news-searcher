package ru.spbstu.news.searcher.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ItemToIndex {

    private final String url;
    private final List<String> imageUrls;
    private final String text;

}
