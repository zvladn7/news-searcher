package ru.spbstu.news.searcher.scanner;

import java.util.Set;

public class CrawlerConfig {
    static final Set<String> resources = Set.of(
            "https://news.mail.ru/",
            "https://ria.ru/",
            "https://news.rambler.ru/"
    );
}
