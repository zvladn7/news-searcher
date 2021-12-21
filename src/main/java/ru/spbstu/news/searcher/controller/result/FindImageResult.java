package ru.spbstu.news.searcher.controller.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class FindImageResult {

    private final List<ImageItem> imageItems;
    private final long totalCount;

}
