package ru.spbstu.news.searcher.controller.result;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class FindByTextResult {

    private List<SearchItem> searchItems;
    private long totalCount;

}
