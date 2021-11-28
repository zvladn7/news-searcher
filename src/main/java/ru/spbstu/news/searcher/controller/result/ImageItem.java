package ru.spbstu.news.searcher.controller.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ImageItem {

    private final long id;
    private final String imageUrl;
    private final String title;
    private final String link;

}
