package ru.spbstu.news.searcher.scanner;

import org.springframework.context.annotation.Configuration;

import java.util.Set;

@Configuration
public class CrawlerConfig {

    static final Set<String> resources = Set.of(
            "https://news.mail.ru/",
            "https://ria.ru/",
            "https://news.rambler.ru/"
    );


}
