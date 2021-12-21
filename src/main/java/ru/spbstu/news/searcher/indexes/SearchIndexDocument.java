package ru.spbstu.news.searcher.indexes;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchIndexDocument {

    private final Long databaseId;
    private final String fullText;

}
