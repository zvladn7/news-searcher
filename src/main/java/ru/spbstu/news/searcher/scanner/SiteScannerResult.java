package ru.spbstu.news.searcher.scanner;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class SiteScannerResult {

    private final String url;
    private final String fullText;
    private final List<String> imageUrls;

}
