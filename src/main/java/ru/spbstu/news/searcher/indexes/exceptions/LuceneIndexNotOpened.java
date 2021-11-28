package ru.spbstu.news.searcher.indexes.exceptions;

public class LuceneIndexNotOpened extends LuceneIndexingException {
    public LuceneIndexNotOpened(String message) {
        super(message);
    }
}
